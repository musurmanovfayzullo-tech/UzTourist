import type { LangCode } from '../i18n'
import { AR_FACTS, arDescription, type ArMonumentFact } from '../data/arFacts'

const ENV_KEY = (import.meta.env.VITE_GEMINI_API_KEY as string | undefined) ?? ''
const CUSTOM_KEY_STORAGE = 'uzt_gemini_key'
const MODELS = [
  'gemini-3.6-flash',
  'gemini-flash-latest',
  'gemini-2.5-flash',
]

export function getApiKey(): string {
  const custom = (localStorage.getItem(CUSTOM_KEY_STORAGE) ?? '').trim()
  if (custom) return custom
  return ENV_KEY.trim()
}

export function hasApiKey(): boolean {
  return getApiKey().length > 0
}

export function setCustomApiKey(key: string) {
  const v = key.trim()
  if (v) localStorage.setItem(CUSTOM_KEY_STORAGE, v)
  else localStorage.removeItem(CUSTOM_KEY_STORAGE)
}

const LANG_NAMES: Record<LangCode, string> = {
  uz: 'Uzbek', en: 'English', ru: 'Russian', de: 'German', fr: 'French',
  es: 'Spanish', tr: 'Turkish', zh: 'Chinese', ja: 'Japanese',
}

export interface SceneAnalysis {
  monumentName: string
  city: string
  ancientEra: string
  builder: string
  architectureStyle: string
  heightMeters: number
  historicalDescription: string
  ancientReconstructionDescription: string
  keyFeatures: string[]
  audioGuideScript: string
  isLiveAi: boolean
  isMonument: boolean
  matchedFact: ArMonumentFact | null
  confidence: number
  statusNote: string
}

export interface VisionResult {
  monumentId: string | null
  confidence: number
  source: 'gemini' | 'local' | 'manual'
  description: string
}

let lastError = ''

interface CallOpts {
  json?: boolean
  temperature?: number
  timeoutMs?: number
  systemInstruction?: string
}

const RETRYABLE = new Set([429, 500, 503])

async function callGemini(parts: unknown[], opts: CallOpts = {}): Promise<string> {
  const key = getApiKey()
  if (!key) {
    lastError = 'API kalit kiritilmagan'
    return ''
  }
  lastError = ''
  const { json = false, temperature = 0.1, timeoutMs = 25000, systemInstruction } = opts

  for (const model of MODELS) {
    for (let attempt = 0; attempt < 2; attempt++) {
      const ctrl = new AbortController()
      const timer = setTimeout(() => ctrl.abort(), timeoutMs)
      try {
        const res = await fetch(
          `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent`,
          {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'x-goog-api-key': key },
            signal: ctrl.signal,
            body: JSON.stringify({
              ...(systemInstruction ? { systemInstruction: { parts: [{ text: systemInstruction }] } } : {}),
              contents: [{ parts }],
              generationConfig: {
                temperature,
                topK: 20,
                topP: 0.85,
                maxOutputTokens: 2048,
                ...(json ? { responseMimeType: 'application/json' } : {}),
                thinkingConfig: { thinkingBudget: 0 },
              },
            }),
          },
        )
        clearTimeout(timer)
        if (!res.ok) {
          const body = await res.text().catch(() => '')
          lastError = `${model}: HTTP ${res.status}`
          console.warn('[Gemini]', model, res.status, body.slice(0, 300))
          if (RETRYABLE.has(res.status) && attempt === 0) {
            await new Promise((r) => setTimeout(r, 1200))
            continue
          }
          break
        }
        const data = await res.json()
        const respParts = data?.candidates?.[0]?.content?.parts
        const text: string = Array.isArray(respParts)
          ? respParts
              .filter((p: { text?: string; thought?: boolean }) => typeof p?.text === 'string' && p.thought !== true)
              .map((p: { text: string }) => p.text)
              .join('\n')
          : ''
        if (text.trim()) return text
        lastError = `${model}: bo'sh javob`
        break
      } catch (e) {
        clearTimeout(timer)
        lastError = (e as Error)?.name === 'AbortError' ? `${model}: vaqt tugadi` : `${model}: tarmoq xatosi`
        console.warn('[Gemini]', model, e)
        if (attempt === 0) continue
      }
    }
  }
  return ''
}

function extractJson(text: string): Record<string, unknown> | null {
  let clean = text.trim()
  if (clean.startsWith('```json')) clean = clean.slice(7)
  else if (clean.startsWith('```')) clean = clean.slice(3)
  if (clean.endsWith('```')) clean = clean.slice(0, -3)
  const match = clean.match(/\{[\s\S]*\}/)
  if (!match) return null
  try {
    return JSON.parse(match[0]) as Record<string, unknown>
  } catch {
    return null
  }
}

/** Fuzzy-match an AI-detected monument name/city to a known AR_FACTS entry. */
function matchFact(name: string, city: string): ArMonumentFact | null {
  const hay = `${name} ${city}`.toLowerCase()
  const keywords: Record<string, string[]> = {
    ar_registan_sherdor: ['registan', 'registon', 'sherdor', 'sher-dor', 'tillakori', 'tillya', "ulug'bek", 'ulugh beg'],
    ar_kalyan_minaret: ['kalyan', 'kalon', 'kalān', 'minorai kalon', 'poi kalyan', 'poyi kalon'],
    ar_kalta_minor: ['kalta', 'ichan qal', 'itcha', 'khiva', 'xiva', 'horezm', 'xorazm'],
    ar_gur_amir: ['gur-e-amir', 'gur amir', 'guri amir', "go'ri amir", 'tamerlane', 'temur maqbara', 'timur mausoleum'],
    ar_ark_bukhara: ['ark fortress', 'ark qal', 'ark citadel', 'arx', 'buxoro arki'],
  }
  const hit = (k: string) => new RegExp(`\\b${k.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\b`, 'i').test(hay)
  let best: ArMonumentFact | null = null
  let bestScore = 0
  for (const f of AR_FACTS) {
    const kws = keywords[f.id] ?? []
    const score = kws.reduce((acc, k) => acc + (hit(k) ? 1 : 0), 0)
    if (score > bestScore) {
      bestScore = score
      best = f
    }
  }
  return bestScore > 0 ? best : null
}

/**
 * Real free-form scene analysis via Gemini Vision — describes what is ACTUALLY
 * in the camera frame (like the Android app's analyzeMonument). If the frame is
 * not a monument, the AI honestly reports what it sees instead of guessing.
 */
export async function analyzeScene(imageBase64: string, lang: LangCode): Promise<SceneAnalysis> {
  const langName = LANG_NAMES[lang]
  const prompt = [
    "You are the world's most accurate AI travel guide and visual analyst for Uzbekistan's Silk Road heritage.",
    'Analyze this live camera frame and describe EXACTLY what is visible. DO NOT GUESS or invent monuments.',
    '',
    'RULES:',
    '1. If the frame shows a real Uzbekistan landmark/monument (Registan, Kalyan Minaret, Kalta Minor, Gur-e Amir, Ark fortress, Shah-i-Zinda, etc.):',
    '   - monumentName: exact name; city: city name; ancientEra: era/century; builder: ruler/architect;',
    '   - architectureStyle: style; heightMeters: number (0 if unknown);',
    '   - historicalDescription: 2-3 rich sentences of real history;',
    '   - ancientReconstructionDescription: how it looked in its golden age;',
    '   - keyFeatures: 3 notable facts; audioGuideScript: warm lively audio-guide narration;',
    '   - isMonument: true; aiConfidenceScore: 85-99.',
    '2. If the frame does NOT show a monument (room, desk, street, people, food, random objects):',
    '   - monumentName: what is ACTUALLY visible (e.g. "Office desk and monitor");',
    '   - city: "Camera frame"; ancientEra: "Modern environment"; builder: "Not a historic monument";',
    '   - architectureStyle: "Everyday scene"; heightMeters: 0;',
    '   - historicalDescription: honestly state that no Uzbekistan monument is visible and suggest pointing the camera at a landmark or its photo;',
    '   - ancientReconstructionDescription: "No ancient reconstruction available.";',
    '   - keyFeatures: 3 short honest observations about the frame;',
    '   - audioGuideScript: polite message asking to point the camera at a monument;',
    '   - isMonument: false; aiConfidenceScore: 90.',
    '',
    'Reply ONLY as clean JSON (no markdown):',
    '{"monumentName":"...","city":"...","ancientEra":"...","builder":"...","architectureStyle":"...","heightMeters":0,"historicalDescription":"...","ancientReconstructionDescription":"...","keyFeatures":["...","...","..."],"audioGuideScript":"...","isMonument":true,"aiConfidenceScore":95}',
    '',
    `IMPORTANT: Write ALL text fields in ${langName}.`,
  ].join('\n')

  const text = await callGemini(
    [
      { text: prompt },
      { inline_data: { mime_type: 'image/jpeg', data: imageBase64 } },
    ],
    { json: true, timeoutMs: 30000 },
  )

  const parsed = text ? extractJson(text) : null
  if (!parsed) {
    return honestFallback(lang, lastError || 'AI javob bermadi')
  }

  const str = (k: string, d = '') => {
    const v = parsed[k]
    return typeof v === 'string' && v.trim() ? v.trim() : d
  }
  const name = str('monumentName', 'Noma\'lum kadr')
  const city = str('city', '')
  const isMonument = parsed.isMonument !== false
  const features = Array.isArray(parsed.keyFeatures)
    ? (parsed.keyFeatures as unknown[]).filter((x): x is string => typeof x === 'string').slice(0, 5)
    : []
  const confidence = Math.min(99, Math.max(50, Number(parsed.aiConfidenceScore) || 90))

  return {
    monumentName: name,
    city,
    ancientEra: str('ancientEra', ''),
    builder: str('builder', ''),
    architectureStyle: str('architectureStyle', ''),
    heightMeters: Number(parsed.heightMeters) || 0,
    historicalDescription: str('historicalDescription', ''),
    ancientReconstructionDescription: str('ancientReconstructionDescription', ''),
    keyFeatures: features.length ? features : ['Kadr real vaqtda tahlil qilindi'],
    audioGuideScript: str('audioGuideScript', str('historicalDescription', '')),
    isLiveAi: true,
    isMonument,
    matchedFact: isMonument ? matchFact(name, city) : null,
    confidence,
    statusNote: 'Gemini Jonli AI Vision',
  }
}

function honestFallback(lang: LangCode, note: string): SceneAnalysis {
  const msgs: Record<LangCode, string> = {
    uz: "AI xizmati hozircha javob bermadi. Gemini API kalitini sozlamalarga kiriting yoki internet aloqasini tekshiring.",
    en: 'AI service did not respond. Add your Gemini API key in settings or check your connection.',
    ru: 'Сервис ИИ не ответил. Добавьте ключ Gemini API в настройках или проверьте соединение.',
    de: 'Der KI-Dienst antwortet nicht. Fügen Sie Ihren Gemini-API-Schlüssel hinzu.',
    fr: "Le service IA n'a pas répondu. Ajoutez votre clé API Gemini dans les paramètres.",
    es: 'El servicio de IA no respondió. Agregue su clave API de Gemini en ajustes.',
    tr: 'AI servisi yanıt vermedi. Ayarlara Gemini API anahtarınızı ekleyin.',
    zh: 'AI服务未响应。请在设置中添加Gemini API密钥。',
    ja: 'AIサービスが応答しません。設定にGemini APIキーを追加してください。',
  }
  return {
    monumentName: 'AI Vision',
    city: '',
    ancientEra: '',
    builder: '',
    architectureStyle: '',
    heightMeters: 0,
    historicalDescription: msgs[lang],
    ancientReconstructionDescription: '',
    keyFeatures: [msgs[lang]],
    audioGuideScript: msgs[lang],
    isLiveAi: false,
    isMonument: false,
    matchedFact: null,
    confidence: 0,
    statusNote: note,
  }
}

/** Local fallback: pick the nearest / most relevant fact without a network call. */
export function localIdentify(lang: LangCode, hint?: string): VisionResult {
  const fact = hint
    ? AR_FACTS.find((f) => f.id === hint || f.city.toLowerCase() === hint.toLowerCase()) ?? AR_FACTS[0]
    : AR_FACTS[0]
  return { monumentId: fact.id, confidence: 0.6, source: 'local', description: arDescription(fact, lang) }
}

export function getFactById(id: string | null): ArMonumentFact | null {
  return AR_FACTS.find((f) => f.id === id) ?? null
}

const translateCache = new Map<string, string>()

/** Translate a text into the target language (cached). Returns source on failure/uz. */
export async function translateText(text: string, lang: LangCode): Promise<string> {
  const src = text.trim()
  if (!src || lang === 'uz' || !hasApiKey()) return text
  const key = `${lang}:${src.length}:${src.slice(0, 40)}`
  const hit = translateCache.get(key)
  if (hit) return hit
  const out = await callGemini(
    [{ text: `Translate this Uzbek tour-guide text into ${LANG_NAMES[lang]}. Return ONLY the translation, preserving meaning and tone. No quotes or notes.\n\n${src}` }],
    { temperature: 0.2 },
  )
  const result = out.trim() || text
  translateCache.set(key, result)
  return result
}

export async function askGemini(question: string, lang: LangCode): Promise<string> {
  if (!hasApiKey()) return ''
  return callGemini(
    [{ text: question }],
    {
      temperature: 0.4,
      systemInstruction: `You are an expert Silk Road travel guide for Uzbekistan. Answer in ${LANG_NAMES[lang]}, 2-4 sentences, factual and warm.`,
    },
  )
}

export interface ChatTurn {
  role: 'user' | 'model'
  text: string
}

/**
 * Context-aware guide chat: knows which monument the tourist is looking at and
 * keeps short conversation history for follow-up questions.
 */
export async function askGuide(
  question: string,
  lang: LangCode,
  context: { monumentName?: string; city?: string; summary?: string } = {},
  history: ChatTurn[] = [],
): Promise<string> {
  if (!hasApiKey()) return ''
  const ctx = context.monumentName
    ? `The tourist is currently at/looking at: ${context.monumentName}${context.city ? `, ${context.city}` : ''}.${context.summary ? ` Known facts: ${context.summary}` : ''}`
    : 'The tourist is exploring Uzbekistan.'
  const system = [
    'You are UzTour AI — a knowledgeable, friendly local guide for Uzbekistan (Samarkand, Bukhara, Khiva, Tashkent, Fergana, etc.).',
    ctx,
    `Always answer in ${LANG_NAMES[lang]}. Be concise (2-5 sentences), accurate, and practical: history, tips, opening hours if known, etiquette, prices in UZS when relevant.`,
    'If unsure, say so honestly instead of inventing facts.',
  ].join('\n')

  const key = getApiKey()
  const contents = [
    ...history.slice(-8).map((h) => ({ role: h.role, parts: [{ text: h.text }] })),
    { role: 'user', parts: [{ text: question }] },
  ]
  for (const model of MODELS) {
    try {
      const ctrl = new AbortController()
      const timer = setTimeout(() => ctrl.abort(), 25000)
      const res = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'x-goog-api-key': key },
        signal: ctrl.signal,
        body: JSON.stringify({
          systemInstruction: { parts: [{ text: system }] },
          contents,
          generationConfig: { temperature: 0.5, maxOutputTokens: 1024, thinkingConfig: { thinkingBudget: 0 } },
        }),
      })
      clearTimeout(timer)
      if (!res.ok) { lastError = `${model}: HTTP ${res.status}`; continue }
      const data = await res.json()
      const parts = data?.candidates?.[0]?.content?.parts
      const text = Array.isArray(parts)
        ? parts.filter((p: { text?: string; thought?: boolean }) => typeof p?.text === 'string' && !p.thought).map((p: { text: string }) => p.text).join('\n')
        : ''
      if (text.trim()) return text.trim()
    } catch (e) {
      lastError = `${model}: tarmoq xatosi`
      console.warn('[Gemini chat]', model, e)
    }
  }
  return ''
}

export function getLastError(): string {
  return lastError
}

import type { LangCode } from '../i18n'

const TTS_LOCALES: Record<LangCode, string> = {
  uz: 'uz-UZ',
  en: 'en-US',
  ru: 'ru-RU',
  de: 'de-DE',
  fr: 'fr-FR',
  es: 'es-ES',
  tr: 'tr-TR',
  zh: 'zh-CN',
  ja: 'ja-JP',
}

// Language fallback chains — browsers have no uz-UZ voice, so Uzbek falls back
// to Turkish (closest phonetics), then Russian, then English.
const VOICE_FALLBACKS: Record<LangCode, string[]> = {
  uz: ['uz', 'tr', 'ru', 'en'],
  en: ['en'],
  ru: ['ru', 'en'],
  de: ['de', 'en'],
  fr: ['fr', 'en'],
  es: ['es', 'en'],
  tr: ['tr', 'en'],
  zh: ['zh', 'en'],
  ja: ['ja', 'en'],
}

const QUALITY_RE = /natural|neural|premium|enhanced|google|online|aria|siri|samantha|zira|yandex/i

function scoreVoice(v: SpeechSynthesisVoice, langPrefix: string): number {
  let s = 0
  if (v.lang.toLowerCase().startsWith(langPrefix)) s += 100
  if (QUALITY_RE.test(v.name)) s += 50
  if (!v.localService) s += 20 // network voices are usually higher quality
  if (v.default) s += 5
  return s
}

function pickVoice(lang: LangCode): SpeechSynthesisVoice | null {
  const voices = window.speechSynthesis.getVoices()
  if (!voices.length) return null
  for (const prefix of VOICE_FALLBACKS[lang]) {
    const candidates = voices.filter((v) => v.lang.toLowerCase().startsWith(prefix))
    if (candidates.length) {
      return candidates.reduce((a, b) => (scoreVoice(b, prefix) > scoreVoice(a, prefix) ? b : a))
    }
  }
  return null
}

// ---- Gemini TTS (real neural voice, incl. Uzbek) ----
const TTS_KEY = (import.meta.env.VITE_GEMINI_TTS_KEY as string | undefined) ?? ''
const TTS_MODELS = ['gemini-2.5-flash-preview-tts', 'gemini-2.5-pro-preview-tts']
const TTS_VOICE = (import.meta.env.VITE_GEMINI_TTS_VOICE as string | undefined) ?? 'Kore'

const TTS_LANG_NAME: Record<LangCode, string> = {
  uz: 'Uzbek', en: 'English', ru: 'Russian', de: 'German', fr: 'French',
  es: 'Spanish', tr: 'Turkish', zh: 'Chinese', ja: 'Japanese',
}

let speakToken = 0
let currentAudio: HTMLAudioElement | null = null
let geminiSpeaking = false

function base64ToBytes(b64: string): Uint8Array {
  const bin = atob(b64)
  const bytes = new Uint8Array(bin.length)
  for (let i = 0; i < bin.length; i++) bytes[i] = bin.charCodeAt(i)
  return bytes
}

// Wrap raw 16-bit mono PCM in a WAV container so <audio> can play it.
function pcmToWavBlob(pcm: Uint8Array, sampleRate: number): Blob {
  const dataSize = pcm.length
  const buf = new ArrayBuffer(44 + dataSize)
  const v = new DataView(buf)
  const str = (off: number, s: string) => {
    for (let i = 0; i < s.length; i++) v.setUint8(off + i, s.charCodeAt(i))
  }
  str(0, 'RIFF'); v.setUint32(4, 36 + dataSize, true); str(8, 'WAVE')
  str(12, 'fmt '); v.setUint32(16, 16, true); v.setUint16(20, 1, true)
  v.setUint16(22, 1, true); v.setUint32(24, sampleRate, true)
  v.setUint32(28, sampleRate * 2, true); v.setUint16(32, 2, true); v.setUint16(34, 16, true)
  str(36, 'data'); v.setUint32(40, dataSize, true)
  new Uint8Array(buf, 44).set(pcm)
  return new Blob([buf], { type: 'audio/wav' })
}

async function fetchGeminiAudio(text: string, lang: LangCode): Promise<HTMLAudioElement | null> {
  if (!TTS_KEY) return null
  // Style directive: Gemini TTS interprets this as HOW to speak, not text to read.
  const directive = `Narrate in ${TTS_LANG_NAME[lang]} as a warm, professional tour guide — clear pronunciation, natural pacing, engaging tone:\n\n`
  for (const model of TTS_MODELS) {
    try {
      const res = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'x-goog-api-key': TTS_KEY },
          body: JSON.stringify({
            contents: [{ parts: [{ text: directive + text }] }],
            generationConfig: {
              responseModalities: ['AUDIO'],
              speechConfig: { voiceConfig: { prebuiltVoiceConfig: { voiceName: TTS_VOICE } } },
            },
          }),
        },
      )
      if (!res.ok) continue
      const data = await res.json()
      const parts = data?.candidates?.[0]?.content?.parts
      const part = Array.isArray(parts)
        ? parts.find((p: { inlineData?: { data?: string; mimeType?: string } }) => p?.inlineData?.data)
        : null
      const b64: string | undefined = part?.inlineData?.data
      if (!b64) continue
      const mime: string = part.inlineData.mimeType ?? 'audio/L16;rate=24000'
      const rate = Number(/rate=(\d+)/.exec(mime)?.[1] ?? 24000)
      return new Audio(URL.createObjectURL(pcmToWavBlob(base64ToBytes(b64), rate)))
    } catch {
      continue
    }
  }
  return null
}

function webSpeak(text: string, lang: LangCode, rate: number): void {
  if (!('speechSynthesis' in window)) return
  const utter = new SpeechSynthesisUtterance(text)
  utter.lang = TTS_LOCALES[lang]
  utter.rate = rate
  utter.pitch = 1.0
  const voice = pickVoice(lang)
  if (voice) {
    utter.voice = voice
    utter.lang = voice.lang // match utterance lang to the actual voice for correct phonemes
  }
  // Voices load async in some browsers — if none yet, wait once then speak.
  if (!window.speechSynthesis.getVoices().length) {
    window.speechSynthesis.onvoiceschanged = () => {
      const v = pickVoice(lang)
      if (v) { utter.voice = v; utter.lang = v.lang }
      window.speechSynthesis.speak(utter)
      window.speechSynthesis.onvoiceschanged = null
    }
    return
  }
  window.speechSynthesis.speak(utter)
}

export function speak(text: string, lang: LangCode, rate = 0.9): void {
  stopSpeaking()
  const token = ++speakToken
  if (TTS_KEY) {
    geminiSpeaking = true
    fetchGeminiAudio(text, lang)
      .then((audio) => {
        if (token !== speakToken) return // cancelled while fetching
        if (!audio) { geminiSpeaking = false; webSpeak(text, lang, rate); return }
        currentAudio = audio
        audio.onended = () => { geminiSpeaking = false; currentAudio = null }
        audio.onerror = () => { geminiSpeaking = false; currentAudio = null }
        audio.play().catch(() => { geminiSpeaking = false; currentAudio = null; webSpeak(text, lang, rate) })
      })
      .catch(() => { if (token === speakToken) { geminiSpeaking = false; webSpeak(text, lang, rate) } })
    return
  }
  webSpeak(text, lang, rate)
}

export function stopSpeaking(): void {
  speakToken++ // invalidate any in-flight Gemini fetch
  geminiSpeaking = false
  if (currentAudio) { try { currentAudio.pause() } catch { /* noop */ } currentAudio = null }
  if ('speechSynthesis' in window) window.speechSynthesis.cancel()
}

export function isSpeaking(): boolean {
  if (geminiSpeaking) return true
  return 'speechSynthesis' in window && window.speechSynthesis.speaking
}

export interface SpeechRecognitionResult {
  transcript: string
  isFinal: boolean
}

type RecognitionLike = {
  lang: string
  continuous: boolean
  interimResults: boolean
  onresult: ((e: unknown) => void) | null
  onerror: ((e: unknown) => void) | null
  onend: (() => void) | null
  start: () => void
  stop: () => void
}

export function createRecognizer(
  lang: LangCode,
  onResult: (r: SpeechRecognitionResult) => void,
  onEnd?: () => void,
): RecognitionLike | null {
  const w = window as unknown as Record<string, unknown>
  const Ctor = (w.SpeechRecognition ?? w.webkitSpeechRecognition) as (new () => RecognitionLike) | undefined
  if (!Ctor) return null
  const rec = new Ctor()
  rec.lang = TTS_LOCALES[lang]
  rec.continuous = false
  rec.interimResults = true
  rec.onresult = (e: unknown) => {
    const ev = e as { results: ArrayLike<ArrayLike<{ transcript: string }> & { isFinal: boolean }> }
    const last = ev.results[ev.results.length - 1]
    if (last) onResult({ transcript: last[0]?.transcript ?? '', isFinal: last.isFinal })
  }
  rec.onend = () => onEnd?.()
  return rec
}

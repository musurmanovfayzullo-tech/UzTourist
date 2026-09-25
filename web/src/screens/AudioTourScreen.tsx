import { useEffect, useState } from 'react'
import { ArrowLeft, Play, Pause, Music, Headphones, Loader2 } from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { AUDIO_TOURS, AMBIENT_TRACKS } from '../data/audio'
import { speak, stopSpeaking, isSpeaking } from '../services/speech'
import { translateText } from '../services/gemini'

interface L10n {
  title: string
  transcript: string
  historicalFact: string
}

export default function AudioTourScreen() {
  const t = useT()
  const lang = useAppStore((s) => s.lang)
  const goBack = useAppStore((s) => s.goBack)
  const [playingId, setPlayingId] = useState<string | null>(null)
  const [expanded, setExpanded] = useState<string | null>(null)
  const [l10n, setL10n] = useState<Record<string, L10n>>({})
  const [translating, setTranslating] = useState(false)

  // Re-localize every tour's text whenever the app language changes.
  useEffect(() => {
    let cancelled = false
    stopSpeaking()
    setPlayingId(null)
    if (lang === 'uz') { setL10n({}); setTranslating(false); return }
    setTranslating(true)
    ;(async () => {
      const out: Record<string, L10n> = {}
      for (const a of AUDIO_TOURS) {
        const [title, transcript, historicalFact] = await Promise.all([
          translateText(a.title, lang),
          translateText(a.transcript, lang),
          translateText(a.historicalFact, lang),
        ])
        out[a.id] = { title, transcript, historicalFact }
      }
      if (!cancelled) { setL10n(out); setTranslating(false) }
    })().catch(() => { if (!cancelled) setTranslating(false) })
    return () => { cancelled = true }
  }, [lang])

  const toggle = (id: string, transcript: string) => {
    if (playingId === id && isSpeaking()) {
      stopSpeaking()
      setPlayingId(null)
      return
    }
    speak(transcript, lang)
    setPlayingId(id)
    const check = setInterval(() => {
      if (!isSpeaking()) {
        setPlayingId(null)
        clearInterval(check)
      }
    }, 600)
  }

  const fmt = (s: number) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`

  return (
    <div className="mx-auto min-h-screen max-w-lg px-4 pb-28 pt-5">
      <button onClick={() => { stopSpeaking(); goBack() }} className="mb-4 flex items-center gap-2 text-sm text-slate-400 hover:text-white">
        <ArrowLeft size={18} /> {t('back')}
      </button>
      <h1 className="flex items-center gap-2 text-lg font-bold text-amber-400"><Headphones size={20} /> {t('audio_title')}</h1>
      <p className="mt-1 text-xs text-slate-400">{t('audio_subtitle')}</p>
      {translating && (
        <p className="mt-1.5 flex items-center gap-1.5 text-[11px] font-medium text-sky-400">
          <Loader2 size={12} className="animate-spin" /> {t('audio_translating')}
        </p>
      )}

      <div className="mt-5 space-y-3">
        {AUDIO_TOURS.map((a) => {
          const L = l10n[a.id]
          const title = L?.title ?? a.title
          const transcript = L?.transcript ?? a.transcript
          const fact = L?.historicalFact ?? a.historicalFact
          return (
          <div key={a.id} className="rounded-3xl border border-white/10 bg-white/5 p-4">
            <div className="flex items-center gap-3">
              <button
                onClick={() => toggle(a.id, transcript)}
                className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-full ${playingId === a.id ? 'bg-red-500' : 'bg-amber-500'} text-slate-950`}
              >
                {playingId === a.id ? <Pause size={20} /> : <Play size={20} className="ml-0.5" />}
              </button>
              <div className="min-w-0 flex-1">
                <h3 className="truncate text-sm font-bold">{title}</h3>
                <p className="truncate text-xs text-slate-400">{a.monumentName} • {a.city}</p>
                <p className="text-[10px] text-slate-500">{a.narratorName} • {fmt(a.durationSeconds)}</p>
              </div>
            </div>
            <button onClick={() => setExpanded(expanded === a.id ? null : a.id)} className="mt-2 text-[11px] font-medium text-sky-400">
              {expanded === a.id ? t('audio_hide') : t('audio_transcript')}
            </button>
            {expanded === a.id && (
              <div className="mt-2 rounded-xl bg-white/5 p-3">
                <p className="text-xs leading-relaxed text-slate-300">{transcript}</p>
                <p className="mt-2 border-l-2 border-amber-500 pl-2 text-[11px] italic text-amber-300">💡 {fact}</p>
              </div>
            )}
          </div>
          )
        })}
      </div>

      <h2 className="mt-7 flex items-center gap-2 text-sm font-semibold text-slate-200"><Music size={16} className="text-violet-400" /> {t('audio_ambient')}</h2>
      <div className="mt-3 space-y-2">
        {AMBIENT_TRACKS.map((m) => (
          <div key={m.id} className="flex items-center gap-3 rounded-2xl border border-white/10 bg-white/5 p-3.5">
            <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-violet-500/20 text-violet-300"><Music size={18} /></span>
            <div className="flex-1">
              <p className="text-xs font-semibold">{m.title}</p>
              <p className="text-[10px] text-slate-400">{m.instrument} • {m.mood}</p>
            </div>
            <span className="text-[10px] text-slate-500">{m.duration}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

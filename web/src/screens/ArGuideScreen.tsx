import { useEffect, useRef, useState } from 'react'
import { ArrowLeft, Camera, List, Volume2, Square, History, Info, KeyRound, X, Landmark, MessageCircle, Send, Loader2 } from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { AR_FACTS, arAudioScript, arDescription, type ArMonumentFact } from '../data/arFacts'
import { analyzeScene, askGuide, hasApiKey, setCustomApiKey, type SceneAnalysis, type ChatTurn } from '../services/gemini'
import { speak, stopSpeaking, isSpeaking } from '../services/speech'

function analysisFromFact(f: ArMonumentFact, lang: Parameters<typeof arDescription>[1]): SceneAnalysis {
  return {
    monumentName: f.monumentName,
    city: f.city,
    ancientEra: f.ancientEraLabel,
    builder: f.builder,
    architectureStyle: f.architecturalStyle,
    heightMeters: f.heightMeters,
    historicalDescription: arDescription(f, lang),
    ancientReconstructionDescription: f.description,
    keyFeatures: f.keyFacts,
    audioGuideScript: arAudioScript(f, lang),
    isLiveAi: false,
    isMonument: true,
    matchedFact: f,
    confidence: 0,
    statusNote: 'Lokal baza',
  }
}

export default function ArGuideScreen() {
  const t = useT()
  const lang = useAppStore((s) => s.lang)
  const goBack = useAppStore((s) => s.goBack)
  const arDestinationId = useAppStore((s) => s.arDestinationId)

  const videoRef = useRef<HTMLVideoElement>(null)
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const streamRef = useRef<MediaStream | null>(null)

  const [cameraOn, setCameraOn] = useState(false)
  const [cameraError, setCameraError] = useState('')
  const [scanning, setScanning] = useState(false)
  const [analysis, setAnalysis] = useState<SceneAnalysis | null>(null)
  const [showAncient, setShowAncient] = useState(false)
  const [speaking, setSpeaking] = useState(false)
  const [showInfo, setShowInfo] = useState(false)
  const [keySet, setKeySet] = useState(hasApiKey())
  const [keyInput, setKeyInput] = useState('')
  const [showPicker, setShowPicker] = useState(false)
  const [capturedImage, setCapturedImage] = useState<string | null>(null)
  const [chatOpen, setChatOpen] = useState(false)
  const [chat, setChat] = useState<ChatTurn[]>([])
  const [chatInput, setChatInput] = useState('')
  const [chatBusy, setChatBusy] = useState(false)

  const sendChat = async () => {
    const q = chatInput.trim()
    if (!q || chatBusy || !analysis) return
    setChatInput('')
    const next: ChatTurn[] = [...chat, { role: 'user', text: q }]
    setChat(next)
    setChatBusy(true)
    try {
      const a = await askGuide(
        q,
        lang,
        { monumentName: analysis.monumentName, city: analysis.city, summary: analysis.historicalDescription.slice(0, 400) },
        chat,
      )
      setChat([...next, { role: 'model', text: a || '—' }])
    } finally {
      setChatBusy(false)
    }
  }

  useEffect(() => {
    if (arDestinationId) {
      const match = AR_FACTS.find((f) => f.id.includes(arDestinationId) || arDestinationId.includes(f.city.toLowerCase()))
      setAnalysis(analysisFromFact(match ?? AR_FACTS[0], lang))
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [arDestinationId])

  useEffect(() => {
    startCamera()
    return () => {
      streamRef.current?.getTracks().forEach((tr) => tr.stop())
      stopSpeaking()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const startCamera = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: 'environment', width: { ideal: 1280 }, height: { ideal: 720 } },
        audio: false,
      })
      streamRef.current = stream
      if (videoRef.current) {
        videoRef.current.srcObject = stream
        await videoRef.current.play()
      }
      setCameraOn(true)
      setCameraError('')
    } catch {
      setCameraError(t('ar_camera_error'))
      setCameraOn(false)
    }
  }

  const captureAndIdentify = async () => {
    setScanning(true)
    try {
      let base64 = ''
      let dataUrl = ''
      const video = videoRef.current
      const canvas = canvasRef.current
      if (video && canvas && cameraOn) {
        canvas.width = video.videoWidth || 1280
        canvas.height = video.videoHeight || 720
        canvas.getContext('2d')?.drawImage(video, 0, 0)
        dataUrl = canvas.toDataURL('image/jpeg', 0.85)
        base64 = dataUrl.split(',')[1] ?? ''
      }
      if (base64) {
        setCapturedImage(dataUrl) // freeze the exact frame being analyzed
        setAnalysis(await analyzeScene(base64, lang))
      }
    } finally {
      setScanning(false)
    }
  }

  const saveKey = () => {
    setCustomApiKey(keyInput)
    setKeySet(hasApiKey())
    setKeyInput('')
  }

  const toggleSpeech = () => {
    if (!analysis) return
    if (isSpeaking()) {
      stopSpeaking()
      setSpeaking(false)
    } else {
      speak(analysis.audioGuideScript, lang)
      setSpeaking(true)
      const check = setInterval(() => {
        if (!isSpeaking()) {
          setSpeaking(false)
          clearInterval(check)
        }
      }, 500)
    }
  }

  const fact = analysis?.matchedFact ?? null

  return (
    <div className="relative h-screen w-full overflow-hidden bg-black">
      <video ref={videoRef} className="absolute inset-0 h-full w-full object-cover" playsInline muted />
      <canvas ref={canvasRef} className="hidden" />

      {/* Frozen captured frame — the exact still photo being analyzed */}
      {capturedImage && (
        <img src={capturedImage} alt="" className="absolute inset-0 z-[5] h-full w-full object-cover" />
      )}

      {!cameraOn && (
        <div className="absolute inset-0 flex flex-col items-center justify-center bg-slate-950 px-8 text-center">
          <Camera size={48} className="text-slate-600" />
          <p className="mt-4 text-sm text-slate-400">{cameraError || t('ar_camera_request')}</p>
          <button onClick={startCamera} className="mt-4 rounded-xl bg-amber-500 px-6 py-2.5 text-sm font-bold text-slate-950">
            {t('ar_enable_camera')}
          </button>
        </div>
      )}

      {/* Top bar */}
      <div className="absolute left-0 right-0 top-0 z-20 flex items-center justify-between bg-gradient-to-b from-black/70 to-transparent p-4 pb-8">
        <button onClick={goBack} className="rounded-full bg-black/50 p-2.5 text-white">
          <ArrowLeft size={20} />
        </button>
        <h1 className="text-sm font-bold text-white">{t('ar_title')}</h1>
        <button onClick={() => setShowInfo(!showInfo)} className="rounded-full bg-black/50 p-2.5 text-white">
          <Info size={20} />
        </button>
      </div>

      {/* Scan frame */}
      {cameraOn && !analysis && (
        <div className="absolute inset-0 z-10 flex items-center justify-center">
          <div className={`h-56 w-56 rounded-3xl border-2 ${scanning ? 'animate-pulse border-cyan-400' : 'border-white/40'}`}>
            <div className="h-full w-full rounded-3xl border-8 border-transparent" />
          </div>
        </div>
      )}

      {/* Ancient overlay — only when AI matched a known monument */}
      {fact && showAncient && (
        <img
          src={fact.ancientImage}
          alt=""
          className="absolute inset-0 z-10 h-full w-full object-cover opacity-80 mix-blend-lighten"
          onError={(e) => { (e.target as HTMLImageElement).style.display = 'none' }}
        />
      )}

      {/* API key input — shown when no Gemini key configured */}
      {cameraOn && !analysis && !keySet && (
        <div className="absolute bottom-28 left-4 right-4 z-20 rounded-2xl border border-amber-400/40 bg-slate-950/95 p-3.5 backdrop-blur-xl">
          <div className="flex items-center gap-2">
            <KeyRound size={14} className="shrink-0 text-amber-400" />
            <p className="text-[11px] font-medium text-amber-300">{t('ar_api_key_hint')}</p>
          </div>
          <div className="mt-2 flex gap-2">
            <input
              value={keyInput}
              onChange={(e) => setKeyInput(e.target.value)}
              placeholder="AIza..."
              className="flex-1 rounded-lg border border-white/10 bg-white/10 px-3 py-2 text-xs text-white outline-none placeholder:text-slate-500 focus:border-amber-400"
            />
            <button onClick={saveKey} className="rounded-lg bg-amber-500 px-4 text-xs font-bold text-slate-950">
              {t('save')}
            </button>
          </div>
        </div>
      )}

      {/* Analysis card */}
      {analysis && (
        <div className="absolute bottom-0 left-0 right-0 z-20 max-h-[55%] overflow-y-auto rounded-t-3xl border-t border-white/10 bg-slate-950/95 p-5 backdrop-blur-xl">
          <div className="mx-auto mb-3 h-1 w-10 rounded-full bg-white/20" />
          <div className="flex items-start justify-between gap-3">
            <div className="min-w-0">
              <h2 className="text-lg font-bold text-amber-400">{analysis.monumentName}</h2>
              <p className="text-xs text-slate-400">
                {[analysis.city, analysis.ancientEra].filter(Boolean).join(' • ')}
              </p>
            </div>
            <span
              className={`shrink-0 rounded-full px-2.5 py-1 text-[10px] font-semibold ${
                analysis.isLiveAi ? 'bg-cyan-500/20 text-cyan-300' : 'bg-white/10 text-slate-400'
              }`}
            >
              {analysis.isLiveAi ? t('ar_ai_live') : t('ar_ai_offline')}
            </span>
          </div>

          {!analysis.isLiveAi && analysis.statusNote && (
            <p className="mt-1.5 text-[10px] text-slate-500">{analysis.statusNote}</p>
          )}

          {analysis.historicalDescription && (
            <p className="mt-3 text-xs leading-relaxed text-slate-300">{analysis.historicalDescription}</p>
          )}

          <div className="mt-3 grid grid-cols-3 gap-2 text-center">
            <div className="rounded-xl bg-white/5 p-2">
              <p className="text-sm font-bold text-white">{analysis.heightMeters > 0 ? `${analysis.heightMeters}m` : '—'}</p>
              <p className="text-[9px] text-slate-400">{t('ar_height')}</p>
            </div>
            <div className="rounded-xl bg-white/5 p-2">
              <p className="text-sm font-bold text-white">{analysis.confidence > 0 ? `${analysis.confidence}%` : '—'}</p>
              <p className="text-[9px] text-slate-400">{t('ar_confidence')}</p>
            </div>
            <div className="rounded-xl bg-white/5 p-2">
              <p className="text-sm font-bold text-white">{fact ? `${fact.audioDurationSec}s` : '—'}</p>
              <p className="text-[9px] text-slate-400">{t('ar_audio_len')}</p>
            </div>
          </div>

          {showInfo && analysis.keyFeatures.length > 0 && (
            <ul className="mt-3 space-y-1.5">
              {analysis.keyFeatures.map((k) => (
                <li key={k} className="flex gap-2 text-xs text-slate-300"><span className="text-amber-400">◆</span>{k}</li>
              ))}
            </ul>
          )}

          <div className="mt-4 flex gap-2">
            <button
              onClick={toggleSpeech}
              className={`flex flex-1 items-center justify-center gap-2 rounded-xl py-3 text-xs font-bold text-white ${speaking ? 'bg-red-600' : 'bg-gradient-to-r from-amber-500 to-orange-600'}`}
            >
              {speaking ? <Square size={14} /> : <Volume2 size={14} />}
              {speaking ? t('ar_stop') : t('ar_listen')}
            </button>
            {fact && (
              <button
                onClick={() => setShowAncient(!showAncient)}
                className={`flex items-center justify-center gap-1.5 rounded-xl border px-4 py-3 text-xs font-semibold ${showAncient ? 'border-cyan-400 bg-cyan-500/20 text-cyan-300' : 'border-white/15 text-slate-300'}`}
              >
                <History size={14} /> {t('ar_then_now')}
              </button>
            )}
            {keySet && (
              <button
                onClick={() => setChatOpen((v) => !v)}
                className={`flex items-center justify-center gap-1.5 rounded-xl border px-4 py-3 text-xs font-semibold ${chatOpen ? 'border-violet-400 bg-violet-500/20 text-violet-300' : 'border-white/15 text-slate-300'}`}
              >
                <MessageCircle size={14} />
              </button>
            )}
          </div>

          {chatOpen && keySet && (
            <div className="mt-3 rounded-2xl border border-violet-500/30 bg-violet-950/30 p-3">
              <p className="text-[11px] font-semibold text-violet-300">{t('ai_ask_guide')}</p>
              <div className="mt-2 max-h-40 space-y-2 overflow-y-auto">
                {chat.map((m, i) => (
                  <div key={i} className={`rounded-xl px-3 py-2 text-xs leading-relaxed ${m.role === 'user' ? 'ml-6 bg-violet-600/30 text-violet-100' : 'mr-6 bg-white/5 text-slate-200'}`}>
                    {m.text}
                  </div>
                ))}
                {chatBusy && <p className="text-[11px] text-slate-400">{t('ai_typing')}</p>}
              </div>
              <div className="mt-2 flex gap-2">
                <input
                  value={chatInput}
                  onChange={(e) => setChatInput(e.target.value)}
                  onKeyDown={(e) => { if (e.key === 'Enter') void sendChat() }}
                  placeholder={t('ai_placeholder')}
                  className="min-w-0 flex-1 rounded-xl border border-white/10 bg-slate-900 px-3 py-2 text-xs text-white placeholder:text-slate-500"
                />
                <button
                  onClick={() => void sendChat()}
                  disabled={chatBusy || !chatInput.trim()}
                  className="rounded-xl bg-violet-600 px-3 text-white disabled:opacity-50"
                  aria-label={t('ai_send')}
                >
                  {chatBusy ? <Loader2 size={14} className="animate-spin" /> : <Send size={14} />}
                </button>
              </div>
            </div>
          )}
          <button onClick={() => { setAnalysis(null); setShowAncient(false); setCapturedImage(null); setChat([]); setChatOpen(false) }} className="mt-2 w-full rounded-xl border border-white/10 py-2 text-xs text-slate-400">
            {t('ar_scan_again')}
          </button>
        </div>
      )}

      {/* Scan button */}
      {cameraOn && !analysis && (
        <div className="absolute bottom-[calc(2rem+env(safe-area-inset-bottom)+1.5rem)] left-0 right-0 z-20 flex justify-center gap-4">
          <button
            onClick={captureAndIdentify}
            disabled={scanning}
            className="flex h-16 w-16 items-center justify-center rounded-full border-4 border-white bg-amber-500 text-slate-950 shadow-2xl disabled:opacity-60"
          >
            {scanning ? <span className="h-6 w-6 animate-spin rounded-full border-2 border-slate-950 border-t-transparent" /> : <Camera size={26} />}
          </button>
          <button
            onClick={() => setShowPicker(true)}
            className="flex h-16 w-16 items-center justify-center rounded-full border border-white/30 bg-black/50 text-white"
            title={t('ar_pick_manual')}
          >
            <List size={22} />
          </button>
        </div>
      )}

      {/* Monument picker — honest manual browsing, no fake AI */}
      {showPicker && !analysis && (
        <div className="absolute bottom-0 left-0 right-0 z-30 max-h-[60%] overflow-y-auto rounded-t-3xl border-t border-white/10 bg-slate-950/95 p-5 backdrop-blur-xl">
          <div className="mx-auto mb-3 h-1 w-10 rounded-full bg-white/20" />
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-white">{t('ar_pick_manual')}</h3>
            <button onClick={() => setShowPicker(false)} className="rounded-full bg-white/10 p-1.5 text-slate-400">
              <X size={16} />
            </button>
          </div>
          <div className="mt-3 space-y-2">
            {AR_FACTS.map((f) => (
              <button
                key={f.id}
                onClick={() => { setAnalysis(analysisFromFact(f, lang)); setShowPicker(false) }}
                className="flex w-full items-center gap-3 rounded-xl border border-white/10 bg-white/5 p-3 text-left transition-colors hover:border-amber-400/40"
              >
                <Landmark size={18} className="shrink-0 text-amber-400" />
                <div className="min-w-0">
                  <p className="truncate text-xs font-semibold text-white">{f.monumentName}</p>
                  <p className="text-[10px] text-slate-400">{f.city} • {f.ancientEraLabel}</p>
                </div>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

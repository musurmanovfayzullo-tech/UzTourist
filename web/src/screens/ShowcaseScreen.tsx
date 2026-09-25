import { useState } from 'react'
import { ArrowLeft, Star, ScanLine, ChevronLeft, ChevronRight } from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { localizedTitle, localizedCity, localizedSubtitle } from '../data/destinations'

export default function ShowcaseScreen() {
  const t = useT()
  const lang = useAppStore((s) => s.lang)
  const goBack = useAppStore((s) => s.goBack)
  const openAr = useAppStore((s) => s.openAr)
  const destinations = useAppStore((s) => s.destinations)
  const [idx, setIdx] = useState(0)

  const d = destinations[idx % destinations.length]
  const next = () => setIdx((i) => (i + 1) % destinations.length)
  const prev = () => setIdx((i) => (i - 1 + destinations.length) % destinations.length)

  return (
    <div className="relative flex min-h-screen flex-col">
      <div className="relative h-[62vh] w-full overflow-hidden">
        <img
          key={d.id}
          src={d.remoteImageUrl ?? d.image}
          alt={d.title}
          className="h-full w-full object-cover"
          onError={(e) => { (e.target as HTMLImageElement).src = '/images/img_registan_1787819168326.jpg' }}
        />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/20 to-slate-950/40" />
        <button onClick={goBack} className="absolute left-4 top-4 rounded-full bg-black/50 p-2.5 text-white">
          <ArrowLeft size={20} />
        </button>
        <div className="absolute bottom-6 left-5 right-5">
          <span className="rounded-full bg-amber-500 px-3 py-1 text-[10px] font-bold text-slate-950">{d.tag}</span>
          <h1 className="mt-2 text-3xl font-black text-white">{localizedTitle(d, lang)}</h1>
          <p className="text-sm text-slate-300">{localizedCity(d, lang)} — {localizedSubtitle(d, lang)}</p>
          <div className="mt-2 flex items-center gap-3 text-xs text-slate-300">
            <span className="flex items-center gap-1 text-amber-400"><Star size={13} fill="currentColor" />{d.rating.toFixed(2)}</span>
            <span>({d.reviewCount.toLocaleString()})</span>
            {d.unescoYear && <span className="rounded bg-sky-500/20 px-2 py-0.5 text-sky-300">UNESCO {d.unescoYear}</span>}
          </div>
        </div>
        <button onClick={prev} className="absolute left-3 top-1/2 -translate-y-1/2 rounded-full bg-black/50 p-2 text-white">
          <ChevronLeft size={20} />
        </button>
        <button onClick={next} className="absolute right-3 top-1/2 -translate-y-1/2 rounded-full bg-black/50 p-2 text-white">
          <ChevronRight size={20} />
        </button>
      </div>

      <div className="mx-auto w-full max-w-lg flex-1 px-5 pb-10 pt-5">
        <p className="text-sm leading-relaxed text-slate-300">{d.description}</p>
        <div className="mt-4 rounded-2xl border-l-4 border-amber-500 bg-white/5 p-4">
          <p className="text-[10px] font-bold uppercase tracking-wider text-amber-400">{t('showcase_fact')}</p>
          <p className="mt-1 text-xs leading-relaxed text-slate-300">{d.historyFact}</p>
        </div>
        <div className="mt-4 grid grid-cols-2 gap-2">
          {d.highlights.map((h) => (
            <div key={h} className="rounded-xl bg-white/5 px-3 py-2.5 text-xs text-slate-300">◆ {h}</div>
          ))}
        </div>
        <div className="mt-4 grid grid-cols-3 gap-2 text-center">
          <div className="rounded-xl bg-white/5 p-3"><p className="text-sm font-bold">{d.weatherTempC}°C</p><p className="text-[9px] text-slate-400">{d.weatherCondition}</p></div>
          <div className="rounded-xl bg-white/5 p-3"><p className="text-sm font-bold">{d.distanceKmFromTashkent} km</p><p className="text-[9px] text-slate-400">{t('showcase_from_tashkent')}</p></div>
          <div className="rounded-xl bg-white/5 p-3"><p className="text-sm font-bold">{d.afrasiyobDuration}</p><p className="text-[9px] text-slate-400">Afrosiyob</p></div>
        </div>
        <p className="mt-3 text-center text-[10px] text-slate-500">🏛️ {d.architecturalPeriod} • 🕐 {d.bestTimeToVisit}</p>
        <button
          onClick={() => openAr(d.id)}
          className="mt-5 flex w-full items-center justify-center gap-2 rounded-2xl bg-gradient-to-r from-cyan-500 to-blue-600 py-4 text-sm font-bold text-white shadow-lg"
        >
          <ScanLine size={18} /> {t('home_ar_open')}
        </button>
        <div className="mt-4 flex justify-center gap-1.5">
          {destinations.map((dd, i) => (
            <button key={dd.id} onClick={() => setIdx(i)} className={`h-1.5 rounded-full transition-all ${i === idx ? 'w-6 bg-amber-400' : 'w-1.5 bg-white/20'}`} />
          ))}
        </div>
      </div>
    </div>
  )
}

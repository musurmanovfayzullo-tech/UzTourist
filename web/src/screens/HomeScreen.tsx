import { useEffect, useMemo, useState } from 'react'
import {
  Globe, Moon, Sun, ShieldAlert, Headphones, Banknote, Users, Ticket,
  Crown, Map as MapIcon, ScanLine, Star, ChevronRight, WifiOff, Landmark,
} from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { LANGUAGES, type LangCode } from '../i18n'
import {
  localizedTitle, localizedCity, localizedSubtitle,
  CATEGORY_KEYS, CROWD_KEYS, CROWD_COLORS,
  type DestinationCategory,
} from '../data/destinations'
import { fetchMonuments, remoteToDestination } from '../services/supabase'
import { fetchWeather } from '../services/weather'

const CATEGORIES: DestinationCategory[] = ['ALL', 'UNESCO', 'SACRED', 'SILK_ROAD', 'NATURE', 'GASTRONOMY']

export default function HomeScreen() {
  const t = useT()
  const lang = useAppStore((s) => s.lang)
  const setLang = useAppStore((s) => s.setLang)
  const darkMode = useAppStore((s) => s.darkMode)
  const toggleDark = useAppStore((s) => s.toggleDark)
  const offlineMode = useAppStore((s) => s.offlineMode)
  const toggleOffline = useAppStore((s) => s.toggleOffline)
  const navigate = useAppStore((s) => s.navigate)
  const openAr = useAppStore((s) => s.openAr)
  const user = useAppStore((s) => s.user)
  const destinations = useAppStore((s) => s.destinations)
  const mergeRemote = useAppStore((s) => s.mergeRemoteDestinations)
  const applyWeather = useAppStore((s) => s.applyWeather)

  const [category, setCategory] = useState<DestinationCategory>('ALL')
  const [langOpen, setLangOpen] = useState(false)
  const [search, setSearch] = useState('')

  useEffect(() => {
    fetchMonuments()
      .then((remote) => mergeRemote(remote.map(remoteToDestination)))
      .catch(() => undefined)
  }, [mergeRemote])

  useEffect(() => {
    if (offlineMode) return
    let cancelled = false
    const targets = useAppStore.getState().destinations
    Promise.all(
      targets.map(async (d) => {
        const [lat, lon] = d.coordinates
        const w = await fetchWeather(lat, lon)
        return w ? ([d.id, { tempC: w.tempC, condition: w.condition }] as const) : null
      }),
    ).then((rows) => {
      if (cancelled) return
      const byId: Record<string, { tempC: number; condition: string }> = {}
      for (const r of rows) if (r) byId[r[0]] = r[1]
      if (Object.keys(byId).length) applyWeather(byId)
    })
    return () => { cancelled = true }
  }, [offlineMode, applyWeather, destinations.length])

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase()
    return destinations.filter(
      (d) =>
        (category === 'ALL' || d.category === category) &&
        (!q || d.title.toLowerCase().includes(q) || d.city.toLowerCase().includes(q)),
    )
  }, [destinations, category, search])

  const quickActions = [
    { icon: ScanLine, label: t('nav_ar'), color: 'from-cyan-500 to-blue-600', action: () => openAr() },
    { icon: MapIcon, label: t('nav_map'), color: 'from-emerald-500 to-teal-600', action: () => navigate('MAP') },
    { icon: Crown, label: t('home_tariffs'), color: 'from-amber-500 to-orange-600', action: () => navigate('TARIFFS') },
    { icon: Ticket, label: t('home_tickets'), color: 'from-violet-500 to-purple-600', action: () => navigate('TICKETS') },
    { icon: Users, label: t('home_staff'), color: 'from-rose-500 to-pink-600', action: () => navigate('STAFF_BOOKING') },
    { icon: Headphones, label: t('home_audio'), color: 'from-sky-500 to-indigo-600', action: () => navigate('AUDIO_TOUR') },
    { icon: Banknote, label: t('home_budget'), color: 'from-lime-500 to-green-600', action: () => navigate('EXPENSES') },
    { icon: Landmark, label: t('home_showcase'), color: 'from-fuchsia-500 to-purple-700', action: () => navigate('SHOWCASE') },
  ]

  return (
    <div className="mx-auto min-h-screen max-w-lg px-4 pb-28 pt-5">
      {/* Header */}
      <header className="flex items-center justify-between">
        <div>
          <p className="text-xs text-slate-400">{t('home_greeting')}, {user?.firstName || 'Sayyoh'} 👋</p>
          <h1 className="text-lg font-bold text-amber-400">{t('app_title')}</h1>
        </div>
        <div className="flex items-center gap-2">
          <button onClick={toggleOffline} title="Offline" className={`rounded-xl border p-2 ${offlineMode ? 'border-amber-400 text-amber-400' : 'border-white/10 text-slate-400'}`}>
            <WifiOff size={18} />
          </button>
          <button onClick={toggleDark} className="rounded-xl border border-white/10 p-2 text-slate-400">
            {darkMode ? <Sun size={18} /> : <Moon size={18} />}
          </button>
          <div className="relative">
            <button onClick={() => setLangOpen(!langOpen)} className="rounded-xl border border-white/10 p-2 text-slate-400">
              <Globe size={18} />
            </button>
            {langOpen && (
              <div className="absolute right-0 top-11 z-40 w-44 rounded-2xl border border-white/10 bg-slate-900 p-1 shadow-2xl">
                {LANGUAGES.map((l) => (
                  <button
                    key={l.code}
                    onClick={() => { setLang(l.code as LangCode); setLangOpen(false) }}
                    className={`flex w-full items-center gap-2 rounded-xl px-3 py-2 text-left text-xs ${lang === l.code ? 'bg-amber-400/15 text-amber-300' : 'text-slate-300 hover:bg-white/5'}`}
                  >
                    <span>{l.flag}</span> {l.displayName}
                  </button>
                ))}
              </div>
            )}
          </div>
          <button onClick={() => navigate('SOS')} className="rounded-xl bg-red-600/20 border border-red-500/40 p-2 text-red-400">
            <ShieldAlert size={18} />
          </button>
        </div>
      </header>

      {/* Search */}
      <input
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        placeholder={t('home_search')}
        className="mt-4 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm outline-none focus:border-amber-400"
      />

      {/* Quick actions grid */}
      <div className="mt-5 grid grid-cols-4 gap-2">
        {quickActions.map(({ icon: Icon, label, color, action }) => (
          <button key={label} onClick={action} className="flex flex-col items-center gap-1.5">
            <span className={`flex h-13 w-13 items-center justify-center rounded-2xl bg-gradient-to-br ${color} p-3.5 text-white shadow-lg`}>
              <Icon size={20} />
            </span>
            <span className="text-center text-[10px] leading-tight text-slate-300">{label}</span>
          </button>
        ))}
      </div>

      {/* Categories */}
      <div className="mt-6 flex gap-2 overflow-x-auto pb-1 [-ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
        {CATEGORIES.map((c) => (
          <button
            key={c}
            onClick={() => setCategory(c)}
            className={`whitespace-nowrap rounded-full border px-4 py-1.5 text-xs font-medium transition ${
              category === c ? 'border-amber-400 bg-amber-400/15 text-amber-300' : 'border-white/10 text-slate-400'
            }`}
          >
            {t(CATEGORY_KEYS[c])}
          </button>
        ))}
      </div>

      {/* Destination cards */}
      <div className="mt-4 space-y-4">
        {filtered.map((d) => (
          <article key={d.id} className="overflow-hidden rounded-3xl border border-white/10 bg-white/5">
            <div className="relative h-44">
              <img
                src={d.remoteImageUrl ?? d.image}
                alt={d.title}
                className="h-full w-full object-cover"
                loading="lazy"
                onError={(e) => { (e.target as HTMLImageElement).src = '/images/img_registan_1787819168326.jpg' }}
              />
              <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-transparent" />
              <span className="absolute left-3 top-3 rounded-full bg-amber-500 px-3 py-1 text-[10px] font-bold text-slate-950">{d.tag}</span>
              <span
                className="absolute right-3 top-3 rounded-full px-2.5 py-1 text-[10px] font-semibold text-white"
                style={{ backgroundColor: CROWD_COLORS[d.crowdLevel] }}
              >
                {t(CROWD_KEYS[d.crowdLevel])}
              </span>
              <div className="absolute bottom-3 left-3 right-3">
                <h3 className="text-lg font-bold text-white">{localizedTitle(d, lang)}</h3>
                <p className="text-xs text-slate-300">{localizedCity(d, lang)} • {localizedSubtitle(d, lang)}</p>
              </div>
            </div>
            <div className="p-4">
              <div className="flex items-center gap-3 text-xs text-slate-400">
                <span className="flex items-center gap-1 text-amber-400"><Star size={13} fill="currentColor" /> {d.rating.toFixed(2)}</span>
                <span>({d.reviewCount.toLocaleString()})</span>
                {d.unescoYear && <span className="rounded bg-sky-500/20 px-2 py-0.5 text-sky-300">UNESCO {d.unescoYear}</span>}
                <span className="ml-auto">🌡️ {d.weatherTempC}°C</span>
              </div>
              <p className="mt-2 line-clamp-2 text-xs leading-relaxed text-slate-300">{d.description}</p>
              <div className="mt-3 flex flex-wrap gap-1.5">
                {d.highlights.slice(0, 3).map((h) => (
                  <span key={h} className="rounded-full bg-white/5 px-2.5 py-1 text-[10px] text-slate-300">{h}</span>
                ))}
              </div>
              <div className="mt-4 flex gap-2">
                <button
                  onClick={() => openAr(d.id)}
                  className="flex flex-1 items-center justify-center gap-1.5 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 py-2.5 text-xs font-bold text-white"
                >
                  <ScanLine size={14} /> {t('home_ar_open')}
                </button>
                <button
                  onClick={() => navigate('MAP')}
                  className="flex items-center justify-center gap-1 rounded-xl border border-white/10 px-4 py-2.5 text-xs text-slate-300"
                >
                  {t('home_on_map')} <ChevronRight size={14} />
                </button>
              </div>
            </div>
          </article>
        ))}
        {filtered.length === 0 && (
          <p className="py-10 text-center text-sm text-slate-500">{t('home_empty')}</p>
        )}
      </div>
    </div>
  )
}

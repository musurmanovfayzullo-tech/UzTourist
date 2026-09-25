import { useEffect, useState } from 'react'
import { ArrowLeft, Lock, RefreshCw, Users, BookOpen, ScrollText, Landmark } from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { verifyAdminPin } from '../services/telegram'
import {
  fetchBookings, fetchUsers, fetchAdminLogs, fetchMonuments,
  logAdminAction, subscribeBookings,
  type RemoteBooking, type RemoteUser, type AdminLog, type RemoteMonument,
} from '../services/supabase'

export default function AdminScreen() {
  const t = useT()
  const goBack = useAppStore((s) => s.goBack)
  const adminUnlocked = useAppStore((s) => s.adminUnlocked)
  const setAdminUnlocked = useAppStore((s) => s.setAdminUnlocked)

  const [pin, setPin] = useState('')
  const [pinError, setPinError] = useState(false)
  const [tab, setTab] = useState<'bookings' | 'users' | 'monuments' | 'logs'>('bookings')
  const [bookings, setBookings] = useState<RemoteBooking[]>([])
  const [users, setUsers] = useState<RemoteUser[]>([])
  const [monuments, setMonuments] = useState<RemoteMonument[]>([])
  const [logs, setLogs] = useState<AdminLog[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const load = async () => {
    setLoading(true)
    setError('')
    try {
      const [b, u, m, l] = await Promise.allSettled([fetchBookings(), fetchUsers(), fetchMonuments(), fetchAdminLogs()])
      if (b.status === 'fulfilled') setBookings(b.value)
      if (u.status === 'fulfilled') setUsers(u.value)
      if (m.status === 'fulfilled') setMonuments(m.value)
      if (l.status === 'fulfilled') setLogs(l.value)
      if (b.status === 'rejected' && u.status === 'rejected') setError(t('admin_error'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (!adminUnlocked) return
    void load()
    const ch = subscribeBookings(() => void load())
    return () => { ch.unsubscribe() }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [adminUnlocked])

  const unlock = async () => {
    setLoading(true)
    const ok = await verifyAdminPin(pin)
    setLoading(false)
    if (ok) {
      setAdminUnlocked(true)
      logAdminAction('ADMIN_LOGIN', 'Web admin panel ochildi').catch(() => undefined)
    } else {
      setPinError(true)
    }
  }

  if (!adminUnlocked) {
    return (
      <div className="mx-auto flex min-h-screen max-w-md flex-col justify-center px-6">
        <button onClick={goBack} className="absolute left-5 top-6 flex items-center gap-2 text-sm text-slate-400">
          <ArrowLeft size={18} /> {t('back')}
        </button>
        <div className="rounded-3xl border border-white/10 bg-white/5 p-8 text-center">
          <Lock size={44} className="mx-auto text-amber-400" />
          <h1 className="mt-3 text-lg font-bold">{t('admin_title')}</h1>
          <p className="mt-1 text-xs text-slate-400">{t('admin_pin_hint')}</p>
          <input
            type="password"
            value={pin}
            onChange={(e) => { setPin(e.target.value); setPinError(false) }}
            onKeyDown={(e) => e.key === 'Enter' && unlock()}
            placeholder="PIN"
            className={`mt-5 w-full rounded-xl border bg-white/5 px-4 py-3 text-center text-lg tracking-[0.4em] outline-none ${pinError ? 'border-red-500' : 'border-white/10 focus:border-amber-400'}`}
          />
          {pinError && <p className="mt-2 text-xs text-red-400">{t('admin_wrong_pin')}</p>}
          <button onClick={unlock} className="mt-4 w-full rounded-xl bg-amber-500 py-3 text-sm font-bold text-slate-950">
            {t('admin_enter')}
          </button>
        </div>
      </div>
    )
  }

  const tabs = [
    { id: 'bookings' as const, icon: BookOpen, label: t('admin_bookings'), count: bookings.length },
    { id: 'users' as const, icon: Users, label: t('admin_users'), count: users.length },
    { id: 'monuments' as const, icon: Landmark, label: t('admin_monuments'), count: monuments.length },
    { id: 'logs' as const, icon: ScrollText, label: t('admin_logs'), count: logs.length },
  ]

  return (
    <div className="mx-auto min-h-screen max-w-lg px-4 pb-28 pt-5">
      <div className="flex items-center justify-between">
        <button onClick={goBack} className="flex items-center gap-2 text-sm text-slate-400 hover:text-white">
          <ArrowLeft size={18} /> {t('back')}
        </button>
        <button onClick={load} disabled={loading} className="rounded-xl border border-white/10 p-2 text-slate-400">
          <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
        </button>
      </div>
      <h1 className="mt-3 text-lg font-bold text-amber-400">{t('admin_title')}</h1>

      <div className="mt-4 grid grid-cols-4 gap-2">
        {tabs.map(({ id, icon: Icon, label, count }) => (
          <button
            key={id}
            onClick={() => setTab(id)}
            className={`flex flex-col items-center gap-1 rounded-2xl border py-3 ${tab === id ? 'border-amber-400 bg-amber-400/10 text-amber-300' : 'border-white/10 text-slate-400'}`}
          >
            <Icon size={16} />
            <span className="text-[9px] font-medium">{label}</span>
            <span className="text-[10px] font-bold">{count}</span>
          </button>
        ))}
      </div>

      {error && <p className="mt-4 rounded-xl border border-red-500/30 bg-red-950/30 p-3 text-xs text-red-300">{error}</p>}

      <div className="mt-4 space-y-2">
        {tab === 'bookings' && bookings.map((b) => (
          <div key={b.id ?? b.booking_code} className="rounded-2xl border border-white/10 bg-white/5 p-4">
            <div className="flex items-center justify-between">
              <p className="text-xs font-bold">{b.booking_code}</p>
              <span className={`rounded-full px-2 py-0.5 text-[9px] font-bold ${b.booking_status === 'Tasdiqlangan' || b.payment_status === "To'langan" ? 'bg-emerald-500/20 text-emerald-300' : 'bg-amber-500/20 text-amber-300'}`}>
                {b.booking_status}
              </span>
            </div>
            <p className="mt-1 text-xs text-slate-300">{b.tourist_name} • {b.tourist_phone}</p>
            <p className="mt-0.5 text-[10px] text-slate-400">{b.tour_title} • {b.start_date} • {b.people_count} kishi</p>
            {b.notes && <p className="mt-0.5 text-[10px] text-slate-500">{b.notes}</p>}
            <p className="mt-1 text-xs font-bold text-amber-400">{b.total_price.toLocaleString()} UZS • {b.payment_status}</p>
          </div>
        ))}

        {tab === 'users' && users.map((u, i) => (
          <div key={`${u.registered_at ?? ''}-${i}`} className="flex items-center gap-3 rounded-2xl border border-white/10 bg-white/5 p-4">
            <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-sky-500/20 font-bold text-sky-300">
              {(u.full_name || '?')[0]}
            </span>
            <div className="min-w-0 flex-1">
              <p className="truncate text-xs font-bold">{u.full_name}</p>
              <p className="text-[10px] text-slate-400">{[u.phone, u.email, u.language].filter(Boolean).join(' • ') || u.language}</p>
            </div>
          </div>
        ))}

        {tab === 'monuments' && monuments.map((m) => (
          <div key={m.id} className="flex items-center gap-3 rounded-2xl border border-white/10 bg-white/5 p-4">
            {m.image_url && <img src={m.image_url} alt="" className="h-12 w-12 rounded-xl object-cover" />}
            <div className="min-w-0 flex-1">
              <p className="truncate text-xs font-bold">{m.title}</p>
              <p className="text-[10px] text-slate-400">{m.city} • ⭐ {m.rating}</p>
            </div>
          </div>
        ))}

        {tab === 'logs' && logs.map((l, i) => (
          <div key={`${l.created_at ?? ''}-${i}`} className="rounded-2xl border border-white/10 bg-white/5 p-3.5">
            <p className="text-xs font-semibold text-amber-300">{l.action}</p>
            <p className="mt-0.5 text-[10px] text-slate-400">{l.details}</p>
            <p className="mt-0.5 text-[9px] text-slate-500">{l.created_at}</p>
          </div>
        ))}

        {!loading && !error &&
          ((tab === 'bookings' && bookings.length === 0) ||
           (tab === 'users' && users.length === 0) ||
           (tab === 'monuments' && monuments.length === 0) ||
           (tab === 'logs' && logs.length === 0)) && (
          <p className="py-10 text-center text-sm text-slate-500">{t('admin_empty')}</p>
        )}
      </div>
    </div>
  )
}

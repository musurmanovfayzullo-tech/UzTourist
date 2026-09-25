import { useEffect, useState } from 'react'
import { ArrowLeft, PhoneCall, ShieldAlert } from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { EMERGENCY_CONTACTS } from '../data/sos'
import { getCurrentPosition } from '../services/geo'
import { dispatchSosAlert } from '../services/telegram'

export default function SosScreen() {
  const t = useT()
  const goBack = useAppStore((s) => s.goBack)
  const user = useAppStore((s) => s.user)
  const [sent, setSent] = useState(false)
  const [sending, setSending] = useState(false)
  const [countdown, setCountdown] = useState(5)
  const [armed, setArmed] = useState(false)

  useEffect(() => {
    if (!armed) return
    if (countdown <= 0) {
      void triggerSos()
      return
    }
    const id = setTimeout(() => setCountdown((c) => c - 1), 1000)
    return () => clearTimeout(id)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [armed, countdown])

  const triggerSos = async () => {
    setSending(true)
    let lat: number | null = null
    let lon: number | null = null
    try {
      const pos = await getCurrentPosition()
      lat = pos.lat
      lon = pos.lon
    } catch {
      // GPS unavailable
    }
    const ok = await dispatchSosAlert(
      `${user?.firstName ?? 'Sayyoh'} ${user?.lastName ?? ''}`.trim(),
      user?.phone || "noma'lum",
      lat,
      lon,
    )
    setSent(ok)
    setSending(false)
  }

  return (
    <div className="mx-auto min-h-screen max-w-md px-5 pb-24 pt-6">
      <button onClick={goBack} className="mb-4 flex items-center gap-2 text-sm text-slate-400 hover:text-white">
        <ArrowLeft size={18} /> {t('back')}
      </button>

      <div className="rounded-3xl border border-red-500/30 bg-red-950/30 p-6 text-center">
        <ShieldAlert size={48} className="mx-auto text-red-400" />
        <h1 className="mt-3 text-xl font-bold text-red-300">{t('sos_title')}</h1>
        <p className="mt-2 text-sm text-slate-300">{t('sos_desc')}</p>

        {!armed && !sent && (
          <button
            onClick={() => setArmed(true)}
            className="mt-6 w-full rounded-2xl bg-red-600 py-4 text-lg font-bold text-white shadow-lg shadow-red-600/30 transition hover:bg-red-500"
          >
            🆘 {t('sos_activate')}
          </button>
        )}

        {armed && !sent && (
          <div className="mt-6">
            <div className="text-5xl font-black text-red-400">{sending ? '…' : countdown}</div>
            <p className="mt-2 text-xs text-slate-400">{t('sos_sending')}</p>
            {!sending && (
              <button
                onClick={() => { setArmed(false); setCountdown(5) }}
                className="mt-4 rounded-xl border border-white/20 px-6 py-2 text-sm text-slate-300"
              >
                {t('cancel')}
              </button>
            )}
          </div>
        )}

        {sent && (
          <div className="mt-6 rounded-2xl border border-emerald-500/40 bg-emerald-950/40 p-4">
            <p className="text-sm font-semibold text-emerald-300">✅ {t('sos_sent')}</p>
          </div>
        )}
      </div>

      <h2 className="mt-8 mb-3 text-sm font-semibold uppercase tracking-wider text-slate-400">
        {t('sos_contacts')}
      </h2>
      <div className="space-y-2">
        {EMERGENCY_CONTACTS.map((c) => (
          <a
            key={c.id}
            href={`tel:${c.phone}`}
            className="flex items-center gap-4 rounded-2xl border border-white/10 bg-white/5 p-4 transition hover:bg-white/10"
          >
            <span className="text-2xl">{c.icon}</span>
            <div className="flex-1">
              <p className="text-sm font-semibold">{t(c.labelKey)}</p>
              <p className="text-xs text-slate-400">{c.phone}</p>
            </div>
            <PhoneCall size={18} style={{ color: c.color }} />
          </a>
        ))}
      </div>
    </div>
  )
}

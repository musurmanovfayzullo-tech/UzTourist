import { useEffect, useRef, useState } from 'react'
import { ArrowLeft, BadgeCheck, CheckCircle2, Send, User } from 'lucide-react'
import { LANGUAGES, type LangCode } from '../i18n'
import { useAppStore, useT } from '../store/appStore'
import { registerUser } from '../services/supabase'
import { dispatchBookingAlert } from '../services/telegram'

type Step = 1 | 2 | 3 | 4

const GOLD = '#F6C845'
const TURQ = '#00E5FF'

function StarEmblem({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 100 100" className={className} fill="none">
      <rect x="24" y="24" width="52" height="52" stroke={GOLD} strokeWidth="2.5" />
      <rect x="24" y="24" width="52" height="52" stroke={TURQ} strokeWidth="2.5" transform="rotate(45 50 50)" />
      <circle cx="50" cy="50" r="9" stroke={GOLD} strokeWidth="2.5" />
    </svg>
  )
}

export default function RegistrationScreen() {
  const t = useT()
  const register = useAppStore((s) => s.register)
  const navigate = useAppStore((s) => s.navigate)
  const lang = useAppStore((s) => s.lang)
  const setLang = useAppStore((s) => s.setLang)

  const [step, setStep] = useState<Step>(1)
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [nameErr, setNameErr] = useState<'err_enter_firstname' | 'err_enter_lastname' | null>(null)

  const [progress, setProgress] = useState(0)
  const [secondsLeft, setSecondsLeft] = useState(10)
  const [tgSent, setTgSent] = useState(false)
  const dispatched = useRef(false)

  const currentLang = LANGUAGES.find((l) => l.code === lang) ?? LANGUAGES[0]

  const finish = () => {
    const user = {
      firstName: firstName.trim(),
      lastName: lastName.trim(),
      language: lang,
      registeredAt: Date.now(),
      isGuest: false,
    }
    register(user)
    registerUser({
      full_name: `${user.firstName} ${user.lastName}`.trim(),
      language: user.language,
    }).catch(() => undefined)
    navigate('HOME')
  }

  const continueAsGuest = () => {
    register({
      firstName: 'Guest',
      lastName: '',
      language: lang,
      registeredAt: Date.now(),
      isGuest: true,
    })
    navigate('HOME')
  }

  useEffect(() => {
    if (step !== 4) return
    dispatched.current = false
    setTgSent(false)
    const total = 10000
    const interval = 100
    let elapsed = 0
    const timer = setInterval(() => {
      elapsed += interval
      setProgress(Math.min(elapsed / total, 1))
      setSecondsLeft(Math.max(0, Math.ceil((total - elapsed) / 1000)))
      if (elapsed >= 2000 && !dispatched.current) {
        dispatched.current = true
        dispatchBookingAlert("Yangi sayyoh ro'yxatdan o'tdi", [
          `👤 ${firstName.trim()} ${lastName.trim()}`,
          `🌐 ${currentLang.displayName}`,
        ])
          .then(() => setTgSent(true))
          .catch(() => setTgSent(true))
      }
      if (elapsed >= total) {
        clearInterval(timer)
        setTimeout(finish, 400)
      }
    }, interval)
    return () => clearInterval(timer)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [step])

  const nextFromStep1 = () => setStep(2)

  const nextFromStep2 = () => {
    if (!firstName.trim()) {
      setNameErr('err_enter_firstname')
      return
    }
    if (!lastName.trim()) {
      setNameErr('err_enter_lastname')
      return
    }
    setNameErr(null)
    setStep(3)
  }

  const nextFromStep3 = () => setStep(4)

  const statusKey =
    progress < 0.3 ? 'saving_msg_1' : progress < 0.6 ? 'saving_msg_2' : progress < 0.85 ? 'saving_msg_3' : 'saving_msg_4'

  const inputCls =
    'w-full bg-transparent py-3 text-base text-white outline-none placeholder:text-slate-500'
  const fieldCls = (err: boolean) =>
    `flex items-center gap-3 rounded-xl border px-3 transition focus-within:border-[#F6C845] ${
      err ? 'border-red-500' : 'border-white/15'
    }`
  const goldBtn =
    'w-full rounded-2xl bg-gradient-to-r from-[#F6C845] to-[#D4A017] py-4 text-sm font-black text-slate-950 shadow-lg shadow-amber-500/20 transition hover:brightness-110 active:scale-[0.98]'

  const R = 54
  const CIRC = 2 * Math.PI * R

  return (
    <div className="relative min-h-[100dvh] overflow-x-hidden bg-gradient-to-b from-[#050B1A] via-[#0A1B3D] to-[#020914]">
      <StarEmblem className="pointer-events-none absolute -right-16 top-5 h-72 w-72 opacity-[0.06]" />

      <div className="relative mx-auto flex min-h-[100dvh] max-w-md flex-col px-6 pb-[calc(2.5rem+env(safe-area-inset-bottom))] pt-6">
        {step !== 4 && (
          <>
            <div className="flex items-center justify-between">
              {step > 1 ? (
                <button
                  onClick={() => setStep((step - 1) as Step)}
                  aria-label={t('back_button')}
                  className="flex h-10 w-10 items-center justify-center rounded-full bg-white/10 transition hover:bg-white/20"
                >
                  <ArrowLeft className="h-5 w-5 text-white" />
                </button>
              ) : (
                <div className="h-10 w-10" />
              )}
              <div className="text-center">
                <p className="text-[11px] font-black tracking-[2px] text-[#F6C845]">SILK ROAD UZ</p>
                <p className="text-[17px] font-bold text-white">{t('reg_title')}</p>
              </div>
              <div className="rounded-xl border border-[#F6C845]/50 bg-[#F6C845]/15 px-2.5 py-1.5 text-xs font-black text-[#F6C845]">
                {step}/3
              </div>
            </div>
            <div className="mt-4 h-1.5 w-full overflow-hidden rounded-full bg-white/15">
              <div
                className="h-full rounded-full bg-[#F6C845] transition-all duration-500"
                style={{ width: `${step * 33.33}%` }}
              />
            </div>
          </>
        )}

        {step === 1 && (
          <div className="mt-6 flex flex-1 flex-col gap-5">
            <div>
              <h2 className="text-base font-bold text-white">{t('reg_step_1_title')}</h2>
              <p className="mt-0.5 text-xs text-slate-400">{t('reg_step_1_sub')}</p>
            </div>

            <div className="-mx-6 flex gap-2.5 overflow-x-auto px-6 pb-1">
              {LANGUAGES.map((l) => {
                const sel = lang === l.code
                return (
                  <button
                    key={l.code}
                    onClick={() => setLang(l.code as LangCode)}
                    className={`flex w-[90px] shrink-0 flex-col items-center rounded-2xl px-1.5 py-3.5 transition ${
                      sel
                        ? 'border-2 border-[#F6C845] bg-[#F6C845]/20'
                        : 'border border-white/10 bg-[#0B2246]/20'
                    }`}
                  >
                    <span className="text-2xl">{l.flag}</span>
                    <span
                      className={`mt-1.5 text-[11px] leading-tight ${
                        sel ? 'font-black text-[#F6C845]' : 'font-medium text-white'
                      }`}
                    >
                      {l.displayName}
                    </span>
                  </button>
                )
              })}
            </div>

            <div className="mt-auto space-y-3 pt-4">
              <button onClick={nextFromStep1} className={goldBtn}>
                {t('reg_continue_btn')}
              </button>
              <button
                onClick={continueAsGuest}
                className="w-full py-2 text-center text-xs font-medium text-slate-400 transition hover:text-slate-200"
              >
                {t('reg_guest')}
              </button>
            </div>
          </div>
        )}

        {step === 2 && (
          <div className="mt-6 flex flex-1 flex-col gap-5">
            <div>
              <h2 className="text-xl font-bold text-white">{t('reg_step_2_title')}</h2>
              <p className="mt-0.5 text-xs text-slate-400">{t('reg_step_2_sub')}</p>
            </div>

            <div
              className={`rounded-2xl border bg-[#0B2246]/20 p-4 ${
                nameErr ? 'border-red-500' : 'border-[#F6C845]/50'
              }`}
            >
              <label className="mb-1.5 block text-xs text-slate-400">{t('reg_first_name')}</label>
              <div className={fieldCls(false)}>
                <User className="h-4 w-4 shrink-0 text-[#F6C845]" />
                <input
                  value={firstName}
                  onChange={(e) => {
                    setNameErr(null)
                    setFirstName(e.target.value)
                  }}
                  placeholder={t('reg_first_name_hint')}
                  className={inputCls}
                />
              </div>

              <label className="mb-1.5 mt-4 block text-xs text-slate-400">{t('reg_last_name')}</label>
              <div className={fieldCls(false)}>
                <BadgeCheck className="h-4 w-4 shrink-0 text-[#00E5FF]" />
                <input
                  value={lastName}
                  onChange={(e) => {
                    setNameErr(null)
                    setLastName(e.target.value)
                  }}
                  placeholder={t('reg_last_name_hint')}
                  className={inputCls}
                />
              </div>
              {nameErr && <p className="mt-2 text-[11px] text-red-400">{t(nameErr)}</p>}
            </div>

            <div className="mt-auto pt-4">
              <button onClick={nextFromStep2} className={goldBtn}>
                {t('reg_continue_btn')}
              </button>
            </div>
          </div>
        )}

        {step === 3 && (
          <div className="mt-6 flex flex-1 flex-col gap-5">
            <div>
              <h2 className="text-xl font-bold text-white">{t('reg_step_3_title')}</h2>
              <p className="mt-0.5 text-xs text-slate-400">{t('reg_step_3_sub')}</p>
            </div>

            <div className="rounded-2xl border border-[#F6C845]/30 bg-[#F6C845]/10 p-3.5">
              <p className="text-[10px] font-black tracking-widest text-[#F6C845]">
                {t('reg_summary_title')}
              </p>
              <p className="mt-1.5 text-sm font-semibold text-white">
                👤 {firstName} {lastName}
              </p>
              <p className="mt-0.5 text-xs text-slate-400">
                🌐 {t('reg_lang_label')}: {currentLang.flag} {currentLang.displayName}
              </p>
            </div>

            <div className="mt-auto pt-4">
              <button onClick={nextFromStep3} className={goldBtn}>
                {t('reg_finish_btn')}
              </button>
            </div>
          </div>
        )}

        {step === 4 && (
          <div className="flex flex-1 flex-col items-center justify-center py-6">
            <div className="relative flex h-40 w-40 items-center justify-center">
              <StarEmblem className="absolute h-36 w-36 animate-[spin_3s_linear_infinite] opacity-40" />
              <svg viewBox="0 0 120 120" className="absolute h-[130px] w-[130px] -rotate-90">
                <circle cx="60" cy="60" r={R} fill="none" stroke="rgba(0,71,171,0.25)" strokeWidth="6" />
                <circle
                  cx="60"
                  cy="60"
                  r={R}
                  fill="none"
                  stroke={GOLD}
                  strokeWidth="6"
                  strokeLinecap="round"
                  strokeDasharray={CIRC}
                  strokeDashoffset={CIRC * (1 - progress)}
                />
              </svg>
              <div className="relative text-center">
                <p className="text-4xl font-black text-[#F6C845]">{secondsLeft}</p>
                <p className="text-[11px] font-bold text-[#00E5FF]">{t('reg_seconds')}</p>
              </div>
            </div>

            <h2 className="mt-8 text-center text-xl font-black text-white">{t('reg_saving_title')}</h2>
            <p className="mt-2.5 px-6 text-center text-sm font-semibold text-[#00E5FF]">{t(statusKey)}</p>

            <div className="mt-7 w-full px-8">
              <div className="h-2 w-full overflow-hidden rounded-full bg-white/15">
                <div
                  className="h-full rounded-full bg-[#F6C845] transition-all duration-100"
                  style={{ width: `${progress * 100}%` }}
                />
              </div>
              <div className="mt-2 flex items-center justify-between">
                <span className="text-[11px] text-slate-400">{t('reg_loading')}</span>
                <span className="text-xs font-black text-[#F6C845]">{Math.round(progress * 100)}%</span>
              </div>

              <div
                className={`mt-4 flex items-center justify-center gap-2 rounded-xl border px-3.5 py-2 ${
                  tgSent ? 'border-emerald-500 bg-emerald-500/20' : 'border-[#00E5FF]/30 bg-[#0047AB]/15'
                }`}
              >
                {tgSent ? (
                  <CheckCircle2 className="h-4 w-4 text-emerald-400" />
                ) : (
                  <Send className="h-4 w-4 text-[#00E5FF]" />
                )}
                <span
                  className={`text-[11px] font-bold ${tgSent ? 'text-emerald-400' : 'text-[#00E5FF]'}`}
                >
                  {tgSent
                    ? 'Telegram dispetcheriga yuborildi ✓'
                    : 'Telegram dispetcherlik tizimiga uzatilmoqda...'}
                </span>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

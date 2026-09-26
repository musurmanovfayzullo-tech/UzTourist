import { useState } from 'react'
import { BadgeCheck, User } from 'lucide-react'
import { LANGUAGES, type LangCode } from '../i18n'
import { useAppStore, useT } from '../store/appStore'
import { registerUser } from '../services/supabase'
import { dispatchBookingAlert } from '../services/telegram'

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

  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [nameErr, setNameErr] = useState<'err_enter_firstname' | 'err_enter_lastname' | null>(null)

  const currentLang = LANGUAGES.find((l) => l.code === lang) ?? LANGUAGES[0]

  const finish = () => {
    if (!firstName.trim()) {
      setNameErr('err_enter_firstname')
      return
    }
    if (!lastName.trim()) {
      setNameErr('err_enter_lastname')
      return
    }
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
    dispatchBookingAlert("Yangi sayyoh ro'yxatdan o'tdi", [
      `👤 ${user.firstName} ${user.lastName}`,
      `🌐 ${currentLang.displayName}`,
    ]).catch(() => undefined)
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

  const inputCls =
    'w-full bg-transparent py-3 text-base text-white outline-none placeholder:text-slate-500'
  const fieldCls = (err: boolean) =>
    `flex items-center gap-3 rounded-xl border px-3 transition focus-within:border-[#F6C845] ${
      err ? 'border-red-500' : 'border-white/15'
    }`
  const goldBtn =
    'w-full rounded-2xl bg-gradient-to-r from-[#F6C845] to-[#D4A017] py-4 text-sm font-black text-slate-950 shadow-lg shadow-amber-500/20 transition hover:brightness-110 active:scale-[0.98]'

  return (
    <div className="relative min-h-[100dvh] overflow-x-hidden bg-gradient-to-b from-[#050B1A] via-[#0A1B3D] to-[#020914]">
      <StarEmblem className="pointer-events-none absolute -right-16 top-5 h-72 w-72 opacity-[0.06]" />

      <div className="relative mx-auto flex min-h-[100dvh] max-w-md flex-col px-6 pb-[calc(2.5rem+env(safe-area-inset-bottom))] pt-6">
        <div className="flex items-center justify-center text-center">
          <div>
            <p className="text-[11px] font-black tracking-[2px] text-[#F6C845]">SILK ROAD UZ</p>
            <p className="text-[17px] font-bold text-white">{t('reg_title')}</p>
          </div>
        </div>

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

            <div className="mt-auto space-y-3 pt-4">
              <button onClick={finish} className={goldBtn}>
                {t('reg_finish_btn')}
              </button>
              <button
                onClick={continueAsGuest}
                className="w-full py-2 text-center text-xs font-medium text-slate-400 transition hover:text-slate-200"
              >
                {t('reg_guest')}
              </button>
            </div>
          </div>
      </div>
    </div>
  )
}

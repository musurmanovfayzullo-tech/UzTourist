import { useEffect } from 'react'
import { useAppStore, useT } from '../store/appStore'

export default function SplashScreen() {
  const navigate = useAppStore((s) => s.navigate)
  const isRegistered = useAppStore((s) => s.isRegistered)
  const t = useT()

  useEffect(() => {
    const timer = setTimeout(() => navigate(isRegistered ? 'HOME' : 'REGISTRATION'), 2200)
    return () => clearTimeout(timer)
  }, [navigate, isRegistered])

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-slate-950 via-indigo-950 to-slate-950 px-6">
      <div className="animate-[logoIn_.9s_ease-out] rounded-full p-1 ring-4 ring-amber-400/70 shadow-[0_0_60px_rgba(251,191,36,0.35)]">
        <img
          src="/images/ic_app_brand_logo.jpg"
          alt="UzTurist"
          className="h-40 w-40 rounded-full object-cover"
        />
      </div>
      <h1 className="mt-7 text-center text-3xl font-bold tracking-wide text-amber-400">{t('app_title')}</h1>
      <p className="mt-2 text-center text-sm text-slate-400">{t('splash_tagline')}</p>
      <div className="mt-10 h-1 w-40 overflow-hidden rounded-full bg-white/10">
        <div className="h-full w-1/2 animate-[loading_1.4s_ease-in-out_infinite] rounded-full bg-amber-400" />
      </div>
      <style>{`@keyframes loading { 0%{transform:translateX(-100%)} 100%{transform:translateX(300%)} } @keyframes logoIn { 0%{opacity:0;transform:scale(.6)} 100%{opacity:1;transform:scale(1)} }`}</style>
    </div>
  )
}

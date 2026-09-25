import { Home, Map, ScanLine, Sparkles, Wallet } from 'lucide-react'
import { useAppStore, useT, type AppScreen } from '../store/appStore'

const ITEMS: { screen: AppScreen; icon: typeof Home; key: string }[] = [
  { screen: 'HOME', icon: Home, key: 'nav_discover' },
  { screen: 'MAP', icon: Map, key: 'nav_map' },
  { screen: 'AR_GUIDE', icon: ScanLine, key: 'nav_ar' },
  { screen: 'OFFERS', icon: Sparkles, key: 'nav_offers' },
  { screen: 'EXPENSES', icon: Wallet, key: 'nav_expenses' },
]

export default function BottomNav() {
  const screen = useAppStore((s) => s.screen)
  const navigate = useAppStore((s) => s.navigate)
  const openAr = useAppStore((s) => s.openAr)
  const t = useT()

  return (
    <nav className="fixed bottom-0 inset-x-0 z-50 border-t border-white/10 bg-slate-950/90 backdrop-blur-xl">
      <div className="mx-auto flex max-w-lg items-center justify-around px-2 pt-2 pb-[calc(0.75rem+env(safe-area-inset-bottom)+1.5rem)]">
        {ITEMS.map(({ screen: s, icon: Icon, key }) => {
          const active = screen === s
          return (
            <button
              key={s}
              onClick={() => (s === 'AR_GUIDE' ? openAr() : navigate(s))}
              className={`flex flex-col items-center gap-0.5 rounded-xl px-3 py-1.5 transition-colors ${
                active ? 'text-amber-400' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Icon size={22} strokeWidth={active ? 2.4 : 1.8} />
              <span className="text-[10px] font-medium">{t(key)}</span>
            </button>
          )
        })}
      </div>
    </nav>
  )
}

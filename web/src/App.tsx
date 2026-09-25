import { useEffect } from 'react'
import { ShieldAlert } from 'lucide-react'
import { useAppStore } from './store/appStore'
import SplashScreen from './screens/SplashScreen'
import RegistrationScreen from './screens/RegistrationScreen'
import HomeScreen from './screens/HomeScreen'
import MapScreen from './screens/MapScreen'
import ArGuideScreen from './screens/ArGuideScreen'
import ExpensesScreen from './screens/ExpensesScreen'
import StaffBookingScreen from './screens/StaffBookingScreen'
import TariffsScreen from './screens/TariffsScreen'
import TicketsScreen from './screens/TicketsScreen'
import OffersScreen from './screens/OffersScreen'
import ShowcaseScreen from './screens/ShowcaseScreen'
import AdminScreen from './screens/AdminScreen'
import AudioTourScreen from './screens/AudioTourScreen'
import SosScreen from './screens/SosScreen'
import BottomNav from './components/BottomNav'

const NAV_SCREENS = new Set(['HOME', 'MAP', 'AR_GUIDE', 'OFFERS', 'EXPENSES'])

export default function App() {
  const screen = useAppStore((s) => s.screen)
  const darkMode = useAppStore((s) => s.darkMode)
  const isRegistered = useAppStore((s) => s.isRegistered)
  const navigate = useAppStore((s) => s.navigate)

  useEffect(() => {
    document.documentElement.classList.toggle('dark', darkMode)
  }, [darkMode])

  const effective = !isRegistered && screen !== 'SPLASH' ? 'REGISTRATION' : screen

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      {effective === 'SPLASH' && <SplashScreen />}
      {effective === 'REGISTRATION' && <RegistrationScreen />}
      {effective === 'HOME' && <HomeScreen />}
      {effective === 'MAP' && <MapScreen />}
      {effective === 'AR_GUIDE' && <ArGuideScreen />}
      {effective === 'EXPENSES' && <ExpensesScreen />}
      {effective === 'STAFF_BOOKING' && <StaffBookingScreen />}
      {effective === 'TARIFFS' && <TariffsScreen />}
      {effective === 'TICKETS' && <TicketsScreen />}
      {effective === 'OFFERS' && <OffersScreen />}
      {effective === 'SHOWCASE' && <ShowcaseScreen />}
      {effective === 'ADMIN' && <AdminScreen />}
      {effective === 'AUDIO_TOUR' && <AudioTourScreen />}
      {effective === 'SOS' && <SosScreen />}
      {NAV_SCREENS.has(effective) && (
        <>
          <button
            onClick={() => navigate('SOS')}
            aria-label="SOS"
            className="fixed bottom-[calc(5.5rem+env(safe-area-inset-bottom))] right-4 z-40 flex h-14 w-14 items-center justify-center rounded-full bg-gradient-to-br from-red-500 to-red-700 text-white shadow-lg shadow-red-600/40 ring-2 ring-red-400/50 transition active:scale-95"
          >
            <ShieldAlert size={26} />
          </button>
          <BottomNav />
        </>
      )}
    </div>
  )
}

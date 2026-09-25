import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { t as translate, type LangCode } from '../i18n'
import type { Destination } from '../data/destinations'
import { SAMPLE_DESTINATIONS } from '../data/destinations'
import type { TourBookingOrder } from '../data/staff'
import type { TariffBookingReceipt } from '../data/tariffs'
import type { TicketBookingOrder } from '../data/tickets'
import type { ExpenseItem } from '../data/currency'

export type AppScreen =
  | 'SPLASH'
  | 'REGISTRATION'
  | 'HOME'
  | 'MAP'
  | 'AR_GUIDE'
  | 'EXPENSES'
  | 'STAFF_BOOKING'
  | 'TARIFFS'
  | 'TICKETS'
  | 'OFFERS'
  | 'SHOWCASE'
  | 'ADMIN'
  | 'AUDIO_TOUR'
  | 'SOS'

export interface UserProfile {
  firstName: string
  lastName: string
  phone?: string
  email?: string
  language: LangCode
  registeredAt: number
  isGuest: boolean
}

interface AppState {
  screen: AppScreen
  previousScreen: AppScreen
  lang: LangCode
  darkMode: boolean
  offlineMode: boolean
  user: UserProfile | null
  isRegistered: boolean
  arDestinationId: string | null
  destinations: Destination[]
  staffOrders: TourBookingOrder[]
  tariffReceipts: TariffBookingReceipt[]
  ticketOrders: TicketBookingOrder[]
  expenses: ExpenseItem[]
  depositUzs: number
  sosActive: boolean
  adminUnlocked: boolean

  navigate: (s: AppScreen) => void
  goBack: () => void
  setLang: (l: LangCode) => void
  toggleDark: () => void
  toggleOffline: () => void
  register: (u: UserProfile) => void
  logout: () => void
  openAr: (destId?: string) => void
  setDestinations: (d: Destination[]) => void
  mergeRemoteDestinations: (remote: Destination[]) => void
  applyWeather: (byId: Record<string, { tempC: number; condition: string }>) => void
  addStaffOrder: (o: TourBookingOrder) => void
  cancelStaffOrder: (orderId: string) => void
  addTariffReceipt: (r: TariffBookingReceipt) => void
  addTicketOrder: (o: TicketBookingOrder) => void
  addExpense: (e: ExpenseItem) => void
  topUpDeposit: (amount: number) => void
  setSosActive: (v: boolean) => void
  setAdminUnlocked: (v: boolean) => void
}

export const useAppStore = create<AppState>()(
  persist(
    (set, get) => ({
      screen: 'SPLASH',
      previousScreen: 'HOME',
      lang: 'uz',
      darkMode: true,
      offlineMode: false,
      user: null,
      isRegistered: false,
      arDestinationId: null,
      destinations: SAMPLE_DESTINATIONS,
      staffOrders: [],
      tariffReceipts: [],
      ticketOrders: [],
      expenses: [],
      depositUzs: 0,
      sosActive: false,
      adminUnlocked: false,

      navigate: (s) => set({ previousScreen: get().screen, screen: s }),
      goBack: () => set({ screen: get().previousScreen === 'SPLASH' ? 'HOME' : get().previousScreen }),
      setLang: (l) => set({ lang: l }),
      toggleDark: () => set({ darkMode: !get().darkMode }),
      toggleOffline: () => set({ offlineMode: !get().offlineMode }),
      register: (u) => set({ user: u, isRegistered: true, lang: u.language }),
      logout: () => set({ user: null, isRegistered: false, screen: 'REGISTRATION' }),
      openAr: (destId) => set({ arDestinationId: destId ?? null, previousScreen: get().screen, screen: 'AR_GUIDE' }),
      setDestinations: (d) => set({ destinations: d }),
      mergeRemoteDestinations: (remote) => {
        const merged = [...SAMPLE_DESTINATIONS]
        for (const r of remote) {
          const idx = merged.findIndex((m) => m.id === r.id)
          if (idx >= 0) merged[idx] = r
          else merged.push(r)
        }
        set({ destinations: merged })
      },
      applyWeather: (byId) =>
        set({
          destinations: get().destinations.map((d) => {
            const w = byId[d.id]
            return w ? { ...d, weatherTempC: w.tempC, weatherCondition: w.condition } : d
          }),
        }),
      addStaffOrder: (o) => set({ staffOrders: [o, ...get().staffOrders] }),
      cancelStaffOrder: (orderId) =>
        set({
          staffOrders: get().staffOrders.map((o) =>
            o.orderId === orderId ? { ...o, status: 'CANCELLED' } : o,
          ),
        }),
      addTariffReceipt: (r) => set({ tariffReceipts: [r, ...get().tariffReceipts] }),
      addTicketOrder: (o) => set({ ticketOrders: [o, ...get().ticketOrders] }),
      addExpense: (e) => set({ expenses: [e, ...get().expenses] }),
      topUpDeposit: (amount) => set({ depositUzs: get().depositUzs + amount }),
      setSosActive: (v) => set({ sosActive: v }),
      setAdminUnlocked: (v) => set({ adminUnlocked: v }),
    }),
    {
      name: 'uztourist-store',
      partialize: (s) => ({
        lang: s.lang,
        darkMode: s.darkMode,
        user: s.user,
        isRegistered: s.isRegistered,
        staffOrders: s.staffOrders,
        tariffReceipts: s.tariffReceipts,
        ticketOrders: s.ticketOrders,
        expenses: s.expenses,
        depositUzs: s.depositUzs,
      }),
    },
  ),
)

export function useT() {
  const lang = useAppStore((s) => s.lang)
  return (key: string) => translate(key, lang)
}

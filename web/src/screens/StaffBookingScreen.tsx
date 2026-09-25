import { useState } from 'react'
import { ArrowLeft, Star, Phone, CheckCircle2, XCircle } from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { SAMPLE_STAFF, STAFF_ROLES, ORDER_STATUS, type TourGuideStaff, type StaffRoleType } from '../data/staff'
import { formatUzs } from '../data/tariffs'
import { dispatchBookingAlert } from '../services/telegram'
import { createBooking } from '../services/supabase'

const ROLE_FILTERS: (StaffRoleType | 'ALL')[] = ['ALL', 'HISTORIAN_GUIDE', 'VIP_DRIVER_ESCORT', 'FULL_CONCIERGE', 'PHOTO_GUIDE']

export default function StaffBookingScreen() {
  const t = useT()
  const goBack = useAppStore((s) => s.goBack)
  const user = useAppStore((s) => s.user)
  const staffOrders = useAppStore((s) => s.staffOrders)
  const addStaffOrder = useAppStore((s) => s.addStaffOrder)
  const cancelStaffOrder = useAppStore((s) => s.cancelStaffOrder)

  const [roleFilter, setRoleFilter] = useState<StaffRoleType | 'ALL'>('ALL')
  const [selected, setSelected] = useState<TourGuideStaff | null>(null)
  const [city, setCity] = useState('Samarqand')
  const [date, setDate] = useState('')
  const [hours, setHours] = useState(4)
  const [guests, setGuests] = useState(2)
  const [pickup, setPickup] = useState('')
  const [done, setDone] = useState(false)
  const [tab, setTab] = useState<'staff' | 'orders'>('staff')

  const filtered = SAMPLE_STAFF.filter((s) => roleFilter === 'ALL' || s.roleType === roleFilter)
  const total = (selected?.pricePerHourUzs ?? 0) * hours

  const book = () => {
    if (!selected) return
    const order = {
      orderId: `ORD-${Date.now().toString(36).toUpperCase()}`,
      staffId: selected.id,
      staffName: selected.fullName,
      staffRole: selected.roleType,
      staffPhone: selected.phoneNumber,
      touristName: `${user?.firstName ?? ''} ${user?.lastName ?? ''}`.trim() || 'Sayyoh',
      touristPhone: user?.phone || '',
      selectedCity: city,
      tourDate: date || new Date().toISOString().slice(0, 10),
      tourStartTime: '09:00',
      durationHours: hours,
      touristCount: guests,
      preferredLanguage: user?.language ?? 'uz',
      pickupLocation: pickup || 'Mehmonxona resepshni',
      includedExtras: [],
      specialRequests: '',
      totalAmountUzs: total,
      status: 'PENDING' as const,
      createdAtFormatted: new Date().toLocaleString('uz-UZ'),
    }
    addStaffOrder(order)
    createBooking({
      tourTitle: `Gid: ${selected.fullName}`,
      touristName: order.touristName,
      touristPhone: order.touristPhone,
      startDate: order.tourDate,
      peopleCount: guests,
      totalPrice: total,
      bookingStatus: 'Kutilmoqda',
      paymentStatus: 'Kutilmoqda',
      notes: `${city} • ${hours} soat • ${order.orderId}`,
    }).catch(() => undefined)
    dispatchBookingAlert('Yangi gid buyurtmasi', [
      `🧑‍✈️ ${selected.fullName} (${STAFF_ROLES[selected.roleType].titleUz})`,
      `👤 ${order.touristName} ${order.touristPhone}`,
      `📍 ${city} • ${order.tourDate} • ${hours} soat`,
      `💰 ${formatUzs(total)}`,
    ]).catch(() => undefined)
    setDone(true)
  }

  return (
    <div className="mx-auto min-h-screen max-w-lg px-4 pb-28 pt-5">
      <button onClick={() => (selected && !done ? setSelected(null) : goBack())} className="mb-4 flex items-center gap-2 text-sm text-slate-400 hover:text-white">
        <ArrowLeft size={18} /> {t('back')}
      </button>
      <h1 className="text-lg font-bold text-amber-400">{t('staff_title')}</h1>

      <div className="mt-4 flex gap-2">
        {(['staff', 'orders'] as const).map((tb) => (
          <button
            key={tb}
            onClick={() => setTab(tb)}
            className={`flex-1 rounded-xl border py-2 text-xs font-semibold ${tab === tb ? 'border-amber-400 bg-amber-400/15 text-amber-300' : 'border-white/10 text-slate-400'}`}
          >
            {tb === 'staff' ? t('staff_tab_list') : `${t('staff_tab_orders')} (${staffOrders.length})`}
          </button>
        ))}
      </div>

      {tab === 'orders' && (
        <div className="mt-4 space-y-3">
          {staffOrders.length === 0 && <p className="py-10 text-center text-sm text-slate-500">{t('staff_no_orders')}</p>}
          {staffOrders.map((o) => (
            <div key={o.orderId} className="rounded-2xl border border-white/10 bg-white/5 p-4">
              <div className="flex items-center justify-between">
                <p className="text-sm font-bold">{o.staffName}</p>
                <span className="rounded-full px-2.5 py-1 text-[10px] font-bold text-white" style={{ backgroundColor: ORDER_STATUS[o.status].color }}>
                  {t(ORDER_STATUS[o.status].labelKey)}
                </span>
              </div>
              <p className="mt-1 text-xs text-slate-400">{o.orderId} • {o.selectedCity} • {o.tourDate} • {o.durationHours}h</p>
              <p className="mt-1 text-xs font-semibold text-amber-300">{formatUzs(o.totalAmountUzs)}</p>
              {o.status === 'PENDING' && (
                <button onClick={() => cancelStaffOrder(o.orderId)} className="mt-2 flex items-center gap-1 text-xs text-red-400">
                  <XCircle size={13} /> {t('cancel')}
                </button>
              )}
            </div>
          ))}
        </div>
      )}

      {tab === 'staff' && !selected && (
        <>
          <div className="mt-4 flex gap-2 overflow-x-auto pb-1 [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
            {ROLE_FILTERS.map((r) => (
              <button
                key={r}
                onClick={() => setRoleFilter(r)}
                className={`whitespace-nowrap rounded-full border px-3.5 py-1.5 text-xs ${roleFilter === r ? 'border-amber-400 bg-amber-400/15 text-amber-300' : 'border-white/10 text-slate-400'}`}
              >
                {r === 'ALL' ? t('cat_all') : `${STAFF_ROLES[r].icon} ${STAFF_ROLES[r].titleUz}`}
              </button>
            ))}
          </div>
          <div className="mt-4 space-y-3">
            {filtered.map((s) => (
              <button key={s.id} onClick={() => { setSelected(s); setDone(false) }} className="w-full rounded-3xl border border-white/10 bg-white/5 p-4 text-left transition hover:border-amber-400/40">
                <div className="flex items-center gap-3">
                  <span className="flex h-12 w-12 items-center justify-center rounded-2xl text-xl font-bold text-white" style={{ backgroundColor: s.avatarColorHex }}>
                    {s.fullName[0]}
                  </span>
                  <div className="min-w-0 flex-1">
                    <p className="truncate text-sm font-bold">{s.fullName}</p>
                    <p className="truncate text-[11px] text-amber-300">{STAFF_ROLES[s.roleType].icon} {s.roleBadge}</p>
                    <p className="mt-0.5 flex items-center gap-1 text-[11px] text-slate-400">
                      <Star size={11} className="text-amber-400" fill="currentColor" /> {s.rating} • {s.experienceYears} {t('staff_years')} • {s.completedToursCount} {t('staff_tours')}
                    </p>
                  </div>
                  {s.isAvailableToday && <span className="rounded-full bg-emerald-500/20 px-2 py-1 text-[9px] font-bold text-emerald-300">{t('staff_available')}</span>}
                </div>
                <div className="mt-2 flex flex-wrap gap-1">
                  {s.languages.slice(0, 4).map((l) => <span key={l} className="rounded-full bg-white/5 px-2 py-0.5 text-[9px] text-slate-300">{l}</span>)}
                </div>
                <p className="mt-2 text-xs font-semibold text-sky-300">{formatUzs(s.pricePerHourUzs)}/{t('staff_per_hour')} • {formatUzs(s.pricePerDayUzs)}/{t('staff_per_day')}</p>
              </button>
            ))}
          </div>
        </>
      )}

      {tab === 'staff' && selected && !done && (
        <div className="mt-4 rounded-3xl border border-white/10 bg-white/5 p-5">
          <div className="flex items-center gap-3">
            <span className="flex h-14 w-14 items-center justify-center rounded-2xl text-2xl font-bold text-white" style={{ backgroundColor: selected.avatarColorHex }}>
              {selected.fullName[0]}
            </span>
            <div>
              <p className="font-bold">{selected.fullName}</p>
              <p className="text-xs text-amber-300">{selected.roleBadge}</p>
              <a href={`tel:${selected.phoneNumber}`} className="mt-0.5 flex items-center gap-1 text-xs text-sky-400"><Phone size={11} /> {selected.phoneNumber}</a>
            </div>
          </div>
          <p className="mt-3 text-xs leading-relaxed text-slate-300">{selected.bioUz}</p>
          {selected.vehicleInfo && <p className="mt-2 rounded-xl bg-white/5 p-2.5 text-[11px] text-slate-300">🚘 {selected.vehicleInfo}</p>}

          <div className="mt-4 space-y-3">
            <select value={city} onChange={(e) => setCity(e.target.value)} className="w-full rounded-xl border border-white/10 bg-slate-900 px-3 py-2.5 text-sm">
              {selected.operatingCities.map((c) => <option key={c}>{c}</option>)}
            </select>
            <input type="date" value={date} onChange={(e) => setDate(e.target.value)} className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm" />
            <input value={pickup} onChange={(e) => setPickup(e.target.value)} placeholder={t('staff_pickup')} className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm outline-none focus:border-amber-400" />
            <div className="flex gap-3">
              <label className="flex-1">
                <span className="text-[10px] text-slate-400">{t('staff_hours')}</span>
                <input type="number" min={1} max={12} value={hours} onChange={(e) => setHours(Math.max(1, +e.target.value))} className="mt-1 w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm" />
              </label>
              <label className="flex-1">
                <span className="text-[10px] text-slate-400">{t('staff_guests')}</span>
                <input type="number" min={1} max={20} value={guests} onChange={(e) => setGuests(Math.max(1, +e.target.value))} className="mt-1 w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm" />
              </label>
            </div>
          </div>

          <div className="mt-4 flex items-center justify-between rounded-xl bg-amber-500/10 p-3">
            <span className="text-xs text-slate-300">{t('staff_total')}</span>
            <span className="text-lg font-black text-amber-400">{formatUzs(total)}</span>
          </div>
          <button onClick={book} className="mt-3 w-full rounded-xl bg-gradient-to-r from-amber-500 to-orange-600 py-3.5 text-sm font-bold text-slate-950">
            {t('staff_book')}
          </button>
        </div>
      )}

      {tab === 'staff' && selected && done && (
        <div className="mt-6 rounded-3xl border border-emerald-500/30 bg-emerald-950/30 p-8 text-center">
          <CheckCircle2 size={56} className="mx-auto text-emerald-400" />
          <h2 className="mt-3 text-lg font-bold text-emerald-300">{t('staff_booked')}</h2>
          <p className="mt-1 text-xs text-slate-400">{t('staff_booked_desc')}</p>
          <button onClick={() => { setSelected(null); setTab('orders') }} className="mt-5 rounded-xl bg-emerald-600 px-6 py-2.5 text-sm font-bold text-white">
            {t('staff_view_orders')}
          </button>
        </div>
      )}
    </div>
  )
}

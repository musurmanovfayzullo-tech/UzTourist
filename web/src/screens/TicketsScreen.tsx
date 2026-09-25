import { useMemo, useState } from 'react'
import { ArrowLeft, Plane, Train, ArrowRight, CheckCircle2, Zap, BadgeDollarSign } from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { ALL_TICKET_OFFERS, TICKET_CITIES, type TicketOffer, type TransportType } from '../data/tickets'
import { formatUzs } from '../data/tariffs'
import { dispatchBookingAlert } from '../services/telegram'
import { createBooking } from '../services/supabase'

export default function TicketsScreen() {
  const t = useT()
  const goBack = useAppStore((s) => s.goBack)
  const user = useAppStore((s) => s.user)
  const addTicketOrder = useAppStore((s) => s.addTicketOrder)

  const [tab, setTab] = useState<TransportType>('FLIGHT')
  const [from, setFrom] = useState('Toshkent')
  const [to, setTo] = useState('Samarqand')
  const [selected, setSelected] = useState<TicketOffer | null>(null)
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [docNum, setDocNum] = useState('')
  const [pax, setPax] = useState(1)
  const [done, setDone] = useState(false)
  const [pnr, setPnr] = useState('')

  const offers = useMemo(
    () => ALL_TICKET_OFFERS.filter((o) => o.transportType === tab && o.originCity === from && o.destinationCity === to),
    [tab, from, to],
  )
  const fallback = useMemo(() => ALL_TICKET_OFFERS.filter((o) => o.transportType === tab), [tab])
  const shown = offers.length ? offers : fallback

  const book = () => {
    if (!selected) return
    const code = `UZ${Math.random().toString(36).slice(2, 8).toUpperCase()}`
    setPnr(code)
    const order = {
      orderId: `TKT-${Date.now().toString(36).toUpperCase()}`,
      ticketOfferId: selected.id,
      passengerFirstName: firstName || user?.firstName || 'Sayyoh',
      passengerLastName: lastName || user?.lastName || '',
      docType: 'Passport',
      docNumber: docNum,
      passengerPhone: user?.phone || '',
      passengerEmail: user?.email || '',
      passengerCount: pax,
      seatNumber: `${Math.floor(Math.random() * 30) + 1}A`,
      travelDate: new Date().toISOString().slice(0, 10),
      totalPriceUzs: selected.priceUzs * pax,
      paymentMethod: 'CARD',
      bookingStatus: 'CONFIRMED',
      pnrCode: code,
      qrCodePayload: `TICKET:${selected.serviceCode}:${code}`,
      createdTimestamp: Date.now(),
    }
    addTicketOrder(order)
    createBooking({
      tourTitle: `${selected.transportType === 'FLIGHT' ? 'Aviachipta' : 'Poyezd'}: ${selected.carrierName} ${selected.serviceCode}`,
      touristName: `${order.passengerFirstName} ${order.passengerLastName}`.trim(),
      touristPhone: order.passengerPhone,
      touristEmail: order.passengerEmail,
      startDate: order.travelDate,
      peopleCount: pax,
      totalPrice: order.totalPriceUzs,
      bookingStatus: 'Tasdiqlangan',
      paymentStatus: "To'langan",
      notes: `${selected.originCity}→${selected.destinationCity} • ${selected.departureTime} • PNR: ${code}`,
    }).catch(() => undefined)
    dispatchBookingAlert('Yangi chipta buyurtmasi', [
      `${selected.transportType === 'FLIGHT' ? '✈️' : '🚆'} ${selected.carrierName} ${selected.serviceCode}`,
      `📍 ${selected.originCity} → ${selected.destinationCity} • ${selected.departureTime}`,
      `👤 ${order.passengerFirstName} ${order.passengerLastName} x${pax}`,
      `💰 ${formatUzs(order.totalPriceUzs)} • PNR: ${code}`,
    ]).catch(() => undefined)
    setDone(true)
  }

  return (
    <div className="mx-auto min-h-screen max-w-lg px-4 pb-28 pt-5">
      <button onClick={() => (selected ? (done ? (setSelected(null), setDone(false)) : setSelected(null)) : goBack())} className="mb-4 flex items-center gap-2 text-sm text-slate-400 hover:text-white">
        <ArrowLeft size={18} /> {t('back')}
      </button>
      <h1 className="text-lg font-bold text-amber-400">{t('tickets_title')}</h1>

      {!selected && (
        <>
          <div className="mt-4 grid grid-cols-2 gap-2">
            <button onClick={() => setTab('FLIGHT')} className={`flex items-center justify-center gap-2 rounded-xl border py-3 text-sm font-semibold ${tab === 'FLIGHT' ? 'border-sky-400 bg-sky-500/15 text-sky-300' : 'border-white/10 text-slate-400'}`}>
              <Plane size={16} /> {t('tickets_flights')}
            </button>
            <button onClick={() => setTab('TRAIN')} className={`flex items-center justify-center gap-2 rounded-xl border py-3 text-sm font-semibold ${tab === 'TRAIN' ? 'border-sky-400 bg-sky-500/15 text-sky-300' : 'border-white/10 text-slate-400'}`}>
              <Train size={16} /> {t('tickets_trains')}
            </button>
          </div>

          <div className="mt-3 flex items-center gap-2">
            <select value={from} onChange={(e) => setFrom(e.target.value)} className="flex-1 rounded-xl border border-white/10 bg-slate-900 px-3 py-2.5 text-sm">
              {TICKET_CITIES.map((c) => <option key={c}>{c}</option>)}
            </select>
            <ArrowRight size={16} className="shrink-0 text-slate-500" />
            <select value={to} onChange={(e) => setTo(e.target.value)} className="flex-1 rounded-xl border border-white/10 bg-slate-900 px-3 py-2.5 text-sm">
              {TICKET_CITIES.map((c) => <option key={c}>{c}</option>)}
            </select>
          </div>

          <div className="mt-4 space-y-3">
            {shown.map((o) => (
              <button key={o.id} onClick={() => { setSelected(o); setDone(false) }} className="w-full rounded-3xl border border-white/10 bg-white/5 p-4 text-left transition hover:border-sky-400/40">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className={`flex h-9 w-9 items-center justify-center rounded-xl ${o.transportType === 'FLIGHT' ? 'bg-sky-500/20 text-sky-300' : 'bg-emerald-500/20 text-emerald-300'}`}>
                      {o.transportType === 'FLIGHT' ? <Plane size={16} /> : <Train size={16} />}
                    </span>
                    <div>
                      <p className="text-xs font-bold">{o.carrierName}</p>
                      <p className="text-[10px] text-slate-400">{o.serviceCode} • {o.vehicleModel}</p>
                    </div>
                  </div>
                  <div className="flex gap-1">
                    {o.isFastest && <span className="flex items-center gap-0.5 rounded-full bg-amber-500/20 px-2 py-0.5 text-[9px] font-bold text-amber-300"><Zap size={9} /> {t('tickets_fastest')}</span>}
                    {o.isBestPrice && <span className="flex items-center gap-0.5 rounded-full bg-emerald-500/20 px-2 py-0.5 text-[9px] font-bold text-emerald-300"><BadgeDollarSign size={9} /> {t('tickets_cheapest')}</span>}
                  </div>
                </div>
                <div className="mt-3 flex items-center justify-between">
                  <div className="text-center">
                    <p className="text-lg font-black">{o.departureTime}</p>
                    <p className="text-[10px] text-slate-400">{o.originCity}</p>
                  </div>
                  <div className="flex-1 px-3 text-center">
                    <p className="text-[10px] text-slate-500">{o.duration}</p>
                    <div className="relative mt-1 h-px bg-white/20"><span className="absolute -top-1 left-1/2 -translate-x-1/2 text-[10px]">{o.transportType === 'FLIGHT' ? '✈️' : '🚆'}</span></div>
                    <p className="mt-1 text-[10px] text-slate-500">{o.travelClass}</p>
                  </div>
                  <div className="text-center">
                    <p className="text-lg font-black">{o.arrivalTime}</p>
                    <p className="text-[10px] text-slate-400">{o.destinationCity}</p>
                  </div>
                </div>
                <div className="mt-3 flex items-center justify-between border-t border-white/10 pt-2.5">
                  <span className="text-[10px] text-slate-400">⭐ {o.rating} • {o.availableSeats} {t('tickets_seats')}</span>
                  <span className="text-sm font-black text-amber-400">{formatUzs(o.priceUzs)}</span>
                </div>
              </button>
            ))}
          </div>
        </>
      )}

      {selected && !done && (
        <div className="mt-4 rounded-3xl border border-white/10 bg-white/5 p-5">
          <p className="text-sm font-bold">{selected.carrierName} • {selected.serviceCode}</p>
          <p className="mt-1 text-xs text-slate-400">{selected.originCity} → {selected.destinationCity} • {selected.departureTime}–{selected.arrivalTime} • {selected.travelClass}</p>
          <div className="mt-2 flex flex-wrap gap-1">
            {selected.amenities.map((a) => <span key={a} className="rounded-full bg-white/5 px-2 py-0.5 text-[9px] text-slate-300">{a}</span>)}
          </div>
          <div className="mt-4 space-y-3">
            <input value={firstName} onChange={(e) => setFirstName(e.target.value)} placeholder={t('reg_firstname')} className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm outline-none focus:border-amber-400" />
            <input value={lastName} onChange={(e) => setLastName(e.target.value)} placeholder={t('reg_lastname')} className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm outline-none focus:border-amber-400" />
            <input value={docNum} onChange={(e) => setDocNum(e.target.value)} placeholder={t('tickets_passport')} className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm outline-none focus:border-amber-400" />
            <label>
              <span className="text-[10px] text-slate-400">{t('tickets_passengers')}</span>
              <input type="number" min={1} max={9} value={pax} onChange={(e) => setPax(Math.max(1, +e.target.value))} className="mt-1 w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm" />
            </label>
          </div>
          <div className="mt-4 flex items-center justify-between rounded-xl bg-amber-500/10 p-3">
            <span className="text-xs text-slate-300">{t('staff_total')}</span>
            <span className="text-lg font-black text-amber-400">{formatUzs(selected.priceUzs * pax)}</span>
          </div>
          <button onClick={book} className="mt-3 w-full rounded-xl bg-gradient-to-r from-sky-500 to-blue-600 py-3.5 text-sm font-bold text-white">
            {t('tickets_buy')}
          </button>
        </div>
      )}

      {selected && done && (
        <div className="mt-6 rounded-3xl border border-emerald-500/30 bg-emerald-950/30 p-8 text-center">
          <CheckCircle2 size={56} className="mx-auto text-emerald-400" />
          <h2 className="mt-3 text-lg font-bold text-emerald-300">{t('tickets_success')}</h2>
          <div className="mx-auto mt-4 w-fit rounded-2xl border-2 border-dashed border-white/20 bg-white/5 px-8 py-4">
            <p className="text-[10px] uppercase tracking-widest text-slate-400">PNR</p>
            <p className="text-2xl font-black tracking-widest text-amber-400">{pnr}</p>
            <p className="mt-1 text-[10px] text-slate-400">{selected.serviceCode} • {selected.departureTime}</p>
          </div>
          <button onClick={() => { setSelected(null); setDone(false) }} className="mt-5 rounded-xl bg-emerald-600 px-6 py-2.5 text-sm font-bold text-white">
            {t('done')}
          </button>
        </div>
      )}
    </div>
  )
}

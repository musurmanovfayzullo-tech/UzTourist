import { useState } from 'react'
import { ArrowLeft, Check, Crown, CreditCard, Banknote, CheckCircle2, ChevronDown, ChevronUp } from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { SAMPLE_TARIFFS, PAYMENT_METHODS, formatUsd, formatUzs, USD_TO_UZS, type TourTariff, type PaymentMethod } from '../data/tariffs'
import { dispatchBookingAlert } from '../services/telegram'
import { createBooking } from '../services/supabase'
import { validateCard, formatCardNumber, formatExpiry, cardType } from '../services/payment'

export default function TariffsScreen() {
  const t = useT()
  const goBack = useAppStore((s) => s.goBack)
  const user = useAppStore((s) => s.user)
  const addTariffReceipt = useAppStore((s) => s.addTariffReceipt)

  const [expanded, setExpanded] = useState<string | null>(null)
  const [booking, setBooking] = useState<TourTariff | null>(null)
  const [payMethod, setPayMethod] = useState<PaymentMethod>('CARD')
  const [cardNum, setCardNum] = useState('')
  const [cardExp, setCardExp] = useState('')
  const [cardCvv, setCardCvv] = useState('')
  const [payError, setPayError] = useState('')
  const [guests, setGuests] = useState(2)
  const [startDate, setStartDate] = useState('')
  const [done, setDone] = useState(false)

  const confirmBooking = () => {
    if (!booking) return
    // Validate card details when paying by card — no real charge happens here,
    // so we only accept well-formed card data and mark the booking as pending.
    if (payMethod === 'CARD') {
      const err = validateCard(cardNum, cardExp, cardCvv)
      if (err) {
        setPayError(t(err))
        return
      }
    }
    setPayError('')
    const total = booking.priceUsd * guests
    const digits = cardNum.replace(/\D/g, '')
    const receipt = {
      orderId: `TRF-${Date.now().toString(36).toUpperCase()}`,
      tariffId: booking.id,
      tariffName: booking.name,
      priceUsd: booking.priceUsd,
      touristName: `${user?.firstName ?? ''} ${user?.lastName ?? ''}`.trim() || 'Sayyoh',
      touristPhone: user?.phone || '',
      startDate: startDate || new Date().toISOString().slice(0, 10),
      guestsCount: guests,
      paymentMethod: payMethod,
      cardNumberMasked: payMethod === 'CARD' ? `**** ${digits.slice(-4)}` : undefined,
      cardType: payMethod === 'CARD' ? cardType(digits) : undefined,
      totalAmountUsd: total,
      timestamp: Date.now(),
      // Honest status: nothing is charged client-side — staff confirms payment.
      paymentStatus: 'PENDING',
      qrCodePayload: `UZTOURIST:${booking.id}:${Date.now()}`,
      extraExpenses: booking.extraExpensesEstimate,
      note: '',
    }
    addTariffReceipt(receipt)
    createBooking({
      tourTitle: `Tarif: ${booking.name}`,
      touristName: receipt.touristName,
      touristPhone: receipt.touristPhone,
      startDate: receipt.startDate,
      peopleCount: guests,
      totalPrice: total * USD_TO_UZS,
      bookingStatus: 'Kutilmoqda',
      paymentStatus: 'Kutilmoqda',
      notes: `${receipt.orderId} • $${total} • ${PAYMENT_METHODS[payMethod].title}`,
    }).catch(() => undefined)
    dispatchBookingAlert('Yangi tarif buyurtmasi', [
      `${booking.coverEmoji} ${booking.name}`,
      `👤 ${receipt.touristName} ${receipt.touristPhone}`,
      `👥 ${guests} kishi • 📅 ${receipt.startDate}`,
      `💰 ${formatUsd(total)} (${formatUzs(total * USD_TO_UZS)})`,
      `💳 ${PAYMENT_METHODS[payMethod].title}`,
    ]).catch(() => undefined)
    setDone(true)
  }

  return (
    <div className="mx-auto min-h-screen max-w-lg px-4 pb-28 pt-5">
      <button onClick={() => (booking ? (done ? (setBooking(null), setDone(false)) : setBooking(null)) : goBack())} className="mb-4 flex items-center gap-2 text-sm text-slate-400 hover:text-white">
        <ArrowLeft size={18} /> {t('back')}
      </button>

      {!booking && (
        <>
          <h1 className="text-lg font-bold text-amber-400">{t('tariff_title')}</h1>
          <p className="mt-1 text-xs text-slate-400">{t('tariff_subtitle')}</p>
          <div className="mt-5 space-y-4">
            {SAMPLE_TARIFFS.map((tr) => (
              <article key={tr.id} className={`overflow-hidden rounded-3xl border ${tr.isVip ? 'border-amber-500/40 bg-gradient-to-b from-amber-950/40 to-slate-900' : 'border-white/10 bg-white/5'}`}>
                <div className="p-5">
                  <div className="flex items-start justify-between">
                    <span className="text-4xl">{tr.coverEmoji}</span>
                    <div className="text-right">
                      <p className="text-2xl font-black text-amber-400">{formatUsd(tr.priceUsd)}</p>
                      <p className="text-[10px] text-slate-400">{tr.durationDays} {t('tariff_days')} • {formatUzs(tr.priceUsd * USD_TO_UZS)}</p>
                    </div>
                  </div>
                  <span className={`mt-2 inline-block rounded-full px-3 py-1 text-[10px] font-bold ${tr.isVip ? 'bg-amber-500 text-slate-950' : 'bg-sky-500/20 text-sky-300'}`}>
                    {tr.isVip && <Crown size={10} className="mr-1 inline" />}{tr.titleBadge}
                  </span>
                  <h3 className="mt-2 text-sm font-bold">{tr.name}</h3>
                  <p className="mt-1.5 text-xs leading-relaxed text-slate-300">{tr.description}</p>

                  <button onClick={() => setExpanded(expanded === tr.id ? null : tr.id)} className="mt-3 flex items-center gap-1 text-xs font-semibold text-sky-400">
                    {expanded === tr.id ? <ChevronUp size={14} /> : <ChevronDown size={14} />} {t('tariff_details')}
                  </button>

                  {expanded === tr.id && (
                    <div className="mt-3 space-y-3 border-t border-white/10 pt-3">
                      <div className="grid grid-cols-2 gap-2 text-[11px]">
                        <div className="rounded-xl bg-white/5 p-2.5">🏨 {tr.hotelIncludedDesc}</div>
                        <div className="rounded-xl bg-white/5 p-2.5">🍽️ {tr.foodIncludedDesc}</div>
                        <div className="rounded-xl bg-white/5 p-2.5">🚘 {tr.transportIncludedDesc}</div>
                        <div className="rounded-xl bg-white/5 p-2.5">🎓 {tr.tourGuideIncludedDesc}</div>
                      </div>
                      <div>
                        <p className="text-xs font-bold text-slate-200">{t('tariff_places')}</p>
                        <div className="mt-1.5 space-y-1.5">
                          {tr.placesToVisit.map((p) => (
                            <div key={p.name} className="rounded-xl bg-white/5 p-2.5">
                              <p className="text-xs font-semibold">{p.icon} {p.name}</p>
                              <p className="text-[10px] text-slate-400">{p.description}</p>
                            </div>
                          ))}
                        </div>
                      </div>
                      <div>
                        <p className="text-xs font-bold text-slate-200">{t('tariff_included')}</p>
                        <ul className="mt-1.5 space-y-1">
                          {tr.includedFeatures.map((f) => <li key={f} className="text-[11px] text-slate-300">{f}</li>)}
                        </ul>
                      </div>
                      <div>
                        <p className="text-xs font-bold text-slate-200">{t('tariff_receipts')}</p>
                        <div className="mt-1.5 space-y-1">
                          {tr.extraExpensesEstimate.map((e) => (
                            <div key={e.receiptNumber} className="flex items-center justify-between rounded-lg bg-white/5 px-2.5 py-1.5 text-[10px]">
                              <span className="text-slate-300">{e.title}</span>
                              <span className="font-bold text-emerald-400">{formatUsd(e.amountUsd)}</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    </div>
                  )}

                  <button onClick={() => setBooking(tr)} className="mt-4 w-full rounded-xl bg-gradient-to-r from-amber-500 to-orange-600 py-3 text-sm font-bold text-slate-950">
                    {t('tariff_book')}
                  </button>
                </div>
              </article>
            ))}
          </div>
        </>
      )}

      {booking && !done && (
        <div className="mt-2">
          <h1 className="text-lg font-bold text-amber-400">{t('tariff_checkout')}</h1>
          <div className="mt-4 rounded-3xl border border-white/10 bg-white/5 p-5">
            <p className="text-sm font-bold">{booking.coverEmoji} {booking.name}</p>
            <p className="mt-1 text-xs text-slate-400">{booking.durationDays} {t('tariff_days')} • {formatUsd(booking.priceUsd)}/{t('tariff_per_person')}</p>

            <div className="mt-4 space-y-3">
              <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm" />
              <label>
                <span className="text-[10px] text-slate-400">{t('staff_guests')}</span>
                <input type="number" min={1} max={20} value={guests} onChange={(e) => setGuests(Math.max(1, +e.target.value))} className="mt-1 w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm" />
              </label>
            </div>

            <p className="mt-4 text-xs font-bold text-slate-200">{t('tariff_payment')}</p>
            <div className="mt-2 grid grid-cols-2 gap-2">
              {(Object.keys(PAYMENT_METHODS) as PaymentMethod[]).map((m) => (
                <button
                  key={m}
                  onClick={() => { setPayMethod(m); setPayError('') }}
                  className={`rounded-xl border p-3 text-left ${payMethod === m ? 'border-amber-400 bg-amber-400/10' : 'border-white/10'}`}
                >
                  <span className="text-lg">{m === 'CARD' ? <CreditCard size={18} className="text-sky-400" /> : m === 'CASH' ? <Banknote size={18} className="text-emerald-400" /> : <span>{PAYMENT_METHODS[m].icon}</span>}</span>
                  <p className="mt-1 text-xs font-semibold">{PAYMENT_METHODS[m].title}</p>
                  <p className="text-[9px] text-slate-400">{PAYMENT_METHODS[m].desc}</p>
                </button>
              ))}
            </div>

            {payMethod === 'CARD' && (
              <div className="mt-3 space-y-2">
                <div className="relative">
                  <input
                    value={cardNum}
                    onChange={(e) => setCardNum(formatCardNumber(e.target.value))}
                    placeholder="8600 1234 5678 9012"
                    inputMode="numeric"
                    className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 pr-20 text-sm tracking-widest outline-none focus:border-amber-400"
                  />
                  {cardNum.replace(/\D/g, '').length >= 4 && (
                    <span className="absolute right-3 top-1/2 -translate-y-1/2 rounded bg-white/10 px-1.5 py-0.5 text-[9px] font-bold text-sky-300">{cardType(cardNum)}</span>
                  )}
                </div>
                <div className="grid grid-cols-2 gap-2">
                  <input
                    value={cardExp}
                    onChange={(e) => setCardExp(formatExpiry(e.target.value))}
                    placeholder="MM/YY"
                    inputMode="numeric"
                    className="rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm tracking-widest outline-none focus:border-amber-400"
                  />
                  <input
                    value={cardCvv}
                    onChange={(e) => setCardCvv(e.target.value.replace(/\D/g, '').slice(0, 4))}
                    placeholder="CVV"
                    inputMode="numeric"
                    type="password"
                    className="rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm tracking-widest outline-none focus:border-amber-400"
                  />
                </div>
              </div>
            )}

            {(payMethod === 'PAYME' || payMethod === 'CLICK') && (
              <p className="mt-3 rounded-xl border border-sky-500/20 bg-sky-500/10 px-3 py-2 text-[11px] text-sky-300">{t('pay_redirect_note')}</p>
            )}

            {payError && <p className="mt-2 text-xs font-semibold text-red-400">{payError}</p>}

            <div className="mt-4 flex items-center justify-between rounded-xl bg-amber-500/10 p-3">
              <span className="text-xs text-slate-300">{t('staff_total')}</span>
              <div className="text-right">
                <p className="text-lg font-black text-amber-400">{formatUsd(booking.priceUsd * guests)}</p>
                <p className="text-[10px] text-slate-400">{formatUzs(booking.priceUsd * guests * USD_TO_UZS)}</p>
              </div>
            </div>
            <button onClick={confirmBooking} className="mt-3 w-full rounded-xl bg-gradient-to-r from-amber-500 to-orange-600 py-3.5 text-sm font-bold text-slate-950">
              {t('tariff_confirm')}
            </button>
          </div>
        </div>
      )}

      {booking && done && (
        <div className="mt-6 rounded-3xl border border-emerald-500/30 bg-emerald-950/30 p-8 text-center">
          <CheckCircle2 size={56} className="mx-auto text-emerald-400" />
          <h2 className="mt-3 text-lg font-bold text-emerald-300">{t('tariff_success')}</h2>
          <p className="mt-1 text-xs text-slate-400">{t('tariff_success_desc')}</p>
          <div className="mt-4 rounded-xl bg-white/5 p-3 text-left text-[11px] text-slate-300">
            {booking.extraExpensesEstimate.map((e) => (
              <p key={e.receiptNumber} className="flex justify-between py-0.5"><span><Check size={10} className="mr-1 inline text-emerald-400" />{e.title}</span><span>{formatUsd(e.amountUsd)}</span></p>
            ))}
          </div>
          <button onClick={() => { setBooking(null); setDone(false); goBack() }} className="mt-5 rounded-xl bg-emerald-600 px-6 py-2.5 text-sm font-bold text-white">
            {t('done')}
          </button>
        </div>
      )}
    </div>
  )
}

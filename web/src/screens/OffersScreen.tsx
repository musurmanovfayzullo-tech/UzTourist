import { useState } from 'react'
import { Star, Phone, MapPin, Clock, Car, UtensilsCrossed, BedDouble } from 'lucide-react'
import { useT } from '../store/appStore'
import { TAXI_OFFERS, DINING_OFFERS, LODGING_OFFERS, SERVICE_TYPES, type ServiceType } from '../data/offers'

const TABS: { type: ServiceType; icon: typeof Car }[] = [
  { type: 'ALL', icon: Star },
  { type: 'TAXI', icon: Car },
  { type: 'DINING', icon: UtensilsCrossed },
  { type: 'LODGING', icon: BedDouble },
]

export default function OffersScreen() {
  const t = useT()
  const [tab, setTab] = useState<ServiceType>('ALL')

  const showTaxi = tab === 'ALL' || tab === 'TAXI'
  const showDining = tab === 'ALL' || tab === 'DINING'
  const showLodging = tab === 'ALL' || tab === 'LODGING'

  return (
    <div className="mx-auto min-h-screen max-w-lg px-4 pb-28 pt-5">
      <h1 className="text-lg font-bold text-amber-400">{t('offers_title')}</h1>
      <p className="mt-1 text-xs text-slate-400">{t('offers_subtitle')}</p>

      <div className="mt-4 flex gap-2 overflow-x-auto pb-1 [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
        {TABS.map(({ type, icon: Icon }) => (
          <button
            key={type}
            onClick={() => setTab(type)}
            className={`flex shrink-0 items-center gap-1.5 whitespace-nowrap rounded-full border px-4 py-2 text-xs font-medium ${
              tab === type ? 'border-amber-400 bg-amber-400/15 text-amber-300' : 'border-white/10 text-slate-400'
            }`}
          >
            <Icon size={13} /> {SERVICE_TYPES[type].title}
          </button>
        ))}
      </div>

      {showTaxi && (
        <section className="mt-5">
          <h2 className="mb-2 text-sm font-bold text-slate-200">🚖 {SERVICE_TYPES.TAXI.title}</h2>
          <div className="space-y-3">
            {TAXI_OFFERS.map((o) => (
              <div key={o.id} className={`rounded-3xl border p-4 ${o.isVipFleet ? 'border-amber-500/30 bg-gradient-to-b from-amber-950/30 to-slate-900' : 'border-white/10 bg-white/5'}`}>
                <div className="flex items-start justify-between">
                  <div>
                    <span className={`rounded-full px-2.5 py-0.5 text-[9px] font-bold ${o.isVipFleet ? 'bg-amber-500 text-slate-950' : 'bg-sky-500/20 text-sky-300'}`}>{o.badge}</span>
                    <h3 className="mt-1.5 text-sm font-bold">{o.title}</h3>
                    <p className="text-[10px] text-slate-400">{o.carModel}</p>
                  </div>
                  <div className="text-right">
                    <p className="flex items-center gap-1 text-xs text-amber-400"><Star size={11} fill="currentColor" />{o.rating}</p>
                    <p className="text-[10px] text-slate-400">⏱ {o.etaMinutes} {t('offers_min')}</p>
                  </div>
                </div>
                <p className="mt-2 text-xs leading-relaxed text-slate-300">{o.description}</p>
                <div className="mt-2 flex flex-wrap gap-1">
                  {o.features.map((f) => <span key={f} className="rounded-full bg-white/5 px-2 py-0.5 text-[9px] text-slate-300">{f}</span>)}
                </div>
                <div className="mt-3 flex items-center justify-between border-t border-white/10 pt-2.5">
                  <div>
                    <p className="text-xs font-bold text-emerald-400">{o.priceEstimateUzs}</p>
                    <p className="text-[9px] text-slate-500">👥 {o.passengerSeats} • 🧳 {o.luggageCapacity} • {o.targetCity}</p>
                  </div>
                  <a href={`tel:${o.phoneNumber}`} className="flex items-center gap-1.5 rounded-xl bg-emerald-600 px-4 py-2 text-xs font-bold text-white">
                    <Phone size={13} /> {t('offers_call')}
                  </a>
                </div>
              </div>
            ))}
          </div>
        </section>
      )}

      {showDining && (
        <section className="mt-6">
          <h2 className="mb-2 text-sm font-bold text-slate-200">🍽️ {SERVICE_TYPES.DINING.title}</h2>
          <div className="space-y-3">
            {DINING_OFFERS.map((d) => (
              <div key={d.id} className="overflow-hidden rounded-3xl border border-white/10 bg-white/5">
                <div className="relative h-36">
                  <img src={d.image} alt={d.name} className="h-full w-full object-cover" loading="lazy"
                    onError={(e) => { (e.target as HTMLImageElement).src = '/images/uzbek_cuisine_plov_feast_1787820877737.jpg' }} />
                  <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 to-transparent" />
                  {d.isHalal && <span className="absolute right-3 top-3 rounded-full bg-emerald-500 px-2.5 py-1 text-[9px] font-bold text-white">HALAL ✓</span>}
                  <div className="absolute bottom-2.5 left-3">
                    <h3 className="text-sm font-bold text-white">{d.name}</h3>
                    <p className="text-[10px] text-slate-300">{d.city} • {d.cuisineType}</p>
                  </div>
                </div>
                <div className="p-4">
                  <div className="flex items-center gap-3 text-[10px] text-slate-400">
                    <span className="flex items-center gap-1 text-amber-400"><Star size={11} fill="currentColor" />{d.rating} ({d.reviewCount})</span>
                    <span className="flex items-center gap-1"><Clock size={11} />{d.openingHours}</span>
                  </div>
                  <p className="mt-2 text-xs leading-relaxed text-slate-300">{d.description}</p>
                  <div className="mt-2 flex flex-wrap gap-1">
                    {d.signatureDishes.map((s) => <span key={s} className="rounded-full bg-amber-500/10 px-2 py-0.5 text-[9px] text-amber-300">{s}</span>)}
                  </div>
                  <div className="mt-3 flex items-center justify-between border-t border-white/10 pt-2.5">
                    <div>
                      <p className="text-xs font-bold text-emerald-400">{d.priceRangeUzs}</p>
                      <p className="flex items-center gap-1 text-[9px] text-slate-500"><MapPin size={9} />{d.address}</p>
                    </div>
                    <a href={`tel:${d.phoneNumber}`} className="flex items-center gap-1.5 rounded-xl bg-emerald-600 px-4 py-2 text-xs font-bold text-white">
                      <Phone size={13} /> {t('offers_call')}
                    </a>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </section>
      )}

      {showLodging && (
        <section className="mt-6">
          <h2 className="mb-2 text-sm font-bold text-slate-200">🏨 {SERVICE_TYPES.LODGING.title}</h2>
          <div className="space-y-3">
            {LODGING_OFFERS.map((l) => (
              <div key={l.id} className="overflow-hidden rounded-3xl border border-white/10 bg-white/5">
                <div className="relative h-36">
                  <img src={l.image} alt={l.name} className="h-full w-full object-cover" loading="lazy"
                    onError={(e) => { (e.target as HTMLImageElement).src = '/images/silk_road_hotel_courtyard_1787820893667.jpg' }} />
                  <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 to-transparent" />
                  <span className="absolute right-3 top-3 rounded-full bg-slate-950/70 px-2.5 py-1 text-[10px] text-amber-300">{'★'.repeat(l.stars)}</span>
                  <div className="absolute bottom-2.5 left-3">
                    <h3 className="text-sm font-bold text-white">{l.name}</h3>
                    <p className="text-[10px] text-slate-300">{l.city} • {l.lodgingType}</p>
                  </div>
                </div>
                <div className="p-4">
                  <p className="text-xs leading-relaxed text-slate-300">{l.description}</p>
                  <p className="mt-1.5 text-[10px] font-semibold text-sky-300">🛏️ {l.roomType}</p>
                  <div className="mt-2 flex flex-wrap gap-1">
                    {l.amenities.map((a) => <span key={a} className="rounded-full bg-white/5 px-2 py-0.5 text-[9px] text-slate-300">{a}</span>)}
                  </div>
                  <p className="mt-2 rounded-lg bg-violet-500/10 p-2 text-[10px] text-violet-300">🎁 {l.bonusPerk}</p>
                  <div className="mt-3 flex items-center justify-between border-t border-white/10 pt-2.5">
                    <div>
                      <p className="text-xs font-bold text-emerald-400">{l.pricePerNightUzs}</p>
                      <p className="text-[9px] text-slate-500">⭐ {l.rating} ({l.reviewCount})</p>
                    </div>
                    <a href={`tel:${l.phoneNumber}`} className="flex items-center gap-1.5 rounded-xl bg-emerald-600 px-4 py-2 text-xs font-bold text-white">
                      <Phone size={13} /> {t('offers_call')}
                    </a>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  )
}

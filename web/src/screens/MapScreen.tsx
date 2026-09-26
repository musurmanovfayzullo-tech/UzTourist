import { useEffect, useMemo, useRef, useState } from 'react'
import L from 'leaflet'
import { Navigation, ScanLine, Route, Layers, X, ExternalLink, Loader2 } from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { localizedTitle, localizedCity } from '../data/destinations'
import { getCurrentPosition, watchPosition, clearWatch, distanceMeters, type GeoPosition } from '../services/geo'
import { fetchRoute, formatDuration, externalNavUrl, type RouteResult } from '../services/routing'

const TILES = {
  street: {
    url: 'https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png',
    attribution: '&copy; OpenStreetMap &copy; CARTO',
  },
  satellite: {
    url: 'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
    attribution: '&copy; Esri',
  },
}

function pinIcon(active: boolean): L.DivIcon {
  const color = active ? '#e11d48' : '#d97706'
  const size = active ? 38 : 32
  const html = `<svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" style="filter:drop-shadow(0 2px 4px rgba(0,0,0,.45))"><path d="M12 2C8.1 2 5 5.1 5 9c0 5.2 7 13 7 13s7-7.8 7-13c0-3.9-3.1-7-7-7z" fill="${color}" stroke="#fff" stroke-width="1.4"/><circle cx="12" cy="9" r="2.8" fill="#fff"/></svg>`
  return L.divIcon({ className: '', html, iconSize: [size, size], iconAnchor: [size / 2, size - 1] })
}

const UZ_BOUNDS = L.latLngBounds([37.1, 55.9], [45.6, 73.2])

export default function MapScreen() {
  const t = useT()
  const lang = useAppStore((s) => s.lang)
  const destinations = useAppStore((s) => s.destinations)
  const openAr = useAppStore((s) => s.openAr)
  const mapRef = useRef<HTMLDivElement>(null)
  const mapObj = useRef<L.Map | null>(null)
  const tileRef = useRef<L.TileLayer | null>(null)
  const markersRef = useRef<L.LayerGroup | null>(null)
  const meRef = useRef<{ dot: L.CircleMarker; ring: L.Circle } | null>(null)
  const routeRef = useRef<L.Polyline | null>(null)
  const watchId = useRef<number | null>(null)

  const [userPos, setUserPos] = useState<GeoPosition | null>(null)
  const [selected, setSelected] = useState<string | null>(null)
  const [satellite, setSatellite] = useState(false)
  const [route, setRoute] = useState<RouteResult | null>(null)
  const [routing, setRouting] = useState(false)
  const [routeErr, setRouteErr] = useState(false)

  // --- map init (once) ---
  useEffect(() => {
    if (!mapRef.current || mapObj.current) return
    const map = L.map(mapRef.current, { zoomControl: false, maxBounds: UZ_BOUNDS.pad(0.6), minZoom: 5 })
    map.fitBounds(UZ_BOUNDS, { padding: [10, 10] })
    L.control.zoom({ position: 'topright' }).addTo(map)
    tileRef.current = L.tileLayer(TILES.street.url, { attribution: TILES.street.attribution, maxZoom: 19 }).addTo(map)
    markersRef.current = L.layerGroup().addTo(map)
    mapObj.current = map

    // Live GPS with accuracy ring
    const place = (p: GeoPosition) => {
      setUserPos(p)
      if (!meRef.current) {
        const ring = L.circle([p.lat, p.lon], { radius: p.accuracy, color: '#3b82f6', weight: 1, fillColor: '#3b82f6', fillOpacity: 0.12 }).addTo(map)
        const dot = L.circleMarker([p.lat, p.lon], { radius: 8, color: '#fff', weight: 3, fillColor: '#3b82f6', fillOpacity: 1 }).addTo(map)
        dot.bindTooltip(t('map_you'))
        meRef.current = { dot, ring }
      } else {
        meRef.current.dot.setLatLng([p.lat, p.lon])
        meRef.current.ring.setLatLng([p.lat, p.lon]).setRadius(p.accuracy)
      }
    }
    getCurrentPosition().then(place).catch(() => undefined)
    watchId.current = watchPosition(place)

    return () => {
      clearWatch(watchId.current)
      map.remove()
      mapObj.current = null
      meRef.current = null
      markersRef.current = null
      routeRef.current = null
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // --- markers sync with destinations (Supabase merge may arrive later) ---
  useEffect(() => {
    const group = markersRef.current
    if (!group) return
    group.clearLayers()
    destinations.forEach((d) => {
      const active = d.id === selected
      const icon = pinIcon(active)
      const m = L.marker(d.coordinates, { icon, title: d.title }).addTo(group)
      m.bindTooltip(localizedTitle(d, lang), { direction: 'top', offset: [0, -18], opacity: 0.95 })
      m.on('click', () => {
        setSelected(d.id)
        mapObj.current?.flyTo(d.coordinates, Math.max(mapObj.current.getZoom(), 13), { duration: 0.6 })
      })
    })
  }, [destinations, selected, lang])

  // --- tile layer toggle ---
  useEffect(() => {
    const map = mapObj.current
    if (!map || !tileRef.current) return
    const cfg = satellite ? TILES.satellite : TILES.street
    tileRef.current.remove()
    tileRef.current = L.tileLayer(cfg.url, { attribution: cfg.attribution, maxZoom: 19 }).addTo(map)
  }, [satellite])

  // --- route polyline ---
  useEffect(() => {
    const map = mapObj.current
    if (!map) return
    routeRef.current?.remove()
    routeRef.current = null
    if (route) {
      routeRef.current = L.polyline(route.coords, { color: '#22d3ee', weight: 5, opacity: 0.9 }).addTo(map)
      map.fitBounds(routeRef.current.getBounds(), { padding: [40, 40] })
    }
  }, [route])

  // clear route when selection changes
  useEffect(() => { setRoute(null); setRouteErr(false) }, [selected])

  const sel = destinations.find((d) => d.id === selected)

  const nearest = useMemo(() => {
    if (!userPos) return null
    let best: { id: string; km: number } | null = null
    for (const d of destinations) {
      const km = distanceMeters(userPos.lat, userPos.lon, d.coordinates[0], d.coordinates[1]) / 1000
      if (!best || km < best.km) best = { id: d.id, km }
    }
    return best
  }, [userPos, destinations])

  const buildRoute = async () => {
    if (!sel) return
    setRouting(true)
    setRouteErr(false)
    try {
      let from = userPos
      if (!from) from = await getCurrentPosition()
      const r = await fetchRoute(from, { lat: sel.coordinates[0], lon: sel.coordinates[1] })
      if (r) setRoute(r)
      else setRouteErr(true)
    } catch {
      setRouteErr(true)
    } finally {
      setRouting(false)
    }
  }

  const locateMe = () => {
    const go = (p: { lat: number; lon: number }) => mapObj.current?.flyTo([p.lat, p.lon], 13, { duration: 0.7 })
    if (userPos) go(userPos)
    else getCurrentPosition().then((p) => { setUserPos(p); go(p) }).catch(() => undefined)
  }

  const fitAll = () => {
    const map = mapObj.current
    if (!map || !destinations.length) return
    map.fitBounds(L.latLngBounds(destinations.map((d) => d.coordinates)), { padding: [40, 40] })
  }

  return (
    <div className="relative h-screen w-full">
      <div ref={mapRef} className="absolute inset-0 z-0" />

      <div className="pointer-events-none absolute left-0 right-0 top-0 z-10 bg-gradient-to-b from-slate-950/90 to-transparent p-4 pb-10">
        <h1 className="text-lg font-bold text-amber-400">{t('map_title')}</h1>
        <p className="text-xs text-slate-300">{destinations.length} {t('map_monuments')}</p>
      </div>

      <div className="absolute right-4 top-28 z-10 flex flex-col gap-2">
        <button onClick={() => setSatellite((v) => !v)} title="Satellite" className={`rounded-full border border-white/20 p-3 shadow-xl ${satellite ? 'bg-amber-500 text-slate-950' : 'bg-slate-900/90 text-slate-200'}`}>
          <Layers size={18} />
        </button>
        <button onClick={fitAll} title="All" className="rounded-full border border-white/20 bg-slate-900/90 p-3 text-slate-200 shadow-xl">
          <Route size={18} />
        </button>
      </div>

      <button
        onClick={locateMe}
        className="absolute bottom-28 right-4 z-10 rounded-full border border-white/20 bg-slate-900/90 p-3.5 text-sky-400 shadow-xl"
      >
        <Navigation size={20} />
      </button>

      {!sel && nearest && (
        <button
          onClick={() => setSelected(nearest.id)}
          className="absolute bottom-24 left-3 right-3 z-10 mx-auto flex max-w-md items-center gap-3 rounded-2xl border border-white/10 bg-slate-900/95 px-4 py-3 text-left shadow-2xl backdrop-blur"
        >
          <span className="text-xl">📍</span>
          <div className="min-w-0 flex-1">
            <p className="truncate text-xs font-semibold">{localizedTitle(destinations.find((d) => d.id === nearest.id)!, lang)}</p>
            <p className="text-[11px] text-sky-300">{nearest.km.toFixed(nearest.km < 10 ? 1 : 0)} km</p>
          </div>
        </button>
      )}

      {sel && (
        <div className="absolute bottom-24 left-3 right-3 z-10 mx-auto max-w-md rounded-3xl border border-white/10 bg-slate-900/95 p-4 shadow-2xl backdrop-blur">
          <div className="flex gap-3">
            <img
              src={sel.remoteImageUrl ?? sel.image}
              alt=""
              className="h-20 w-20 rounded-2xl object-cover"
              onError={(e) => { (e.target as HTMLImageElement).src = '/images/img_registan_1787819168326.jpg' }}
            />
            <div className="min-w-0 flex-1">
              <h3 className="truncate text-sm font-bold">{localizedTitle(sel, lang)}</h3>
              <p className="text-xs text-slate-400">{localizedCity(sel, lang)} • ⭐ {sel.rating.toFixed(2)}</p>
              <p className="mt-1 text-xs text-sky-300">
                {route
                  ? `🚗 ${(route.distanceM / 1000).toFixed(route.distanceM < 10000 ? 1 : 0)} km • ${formatDuration(route.durationS)}`
                  : userPos
                    ? `📍 ${(distanceMeters(userPos.lat, userPos.lon, sel.coordinates[0], sel.coordinates[1]) / 1000).toFixed(0)} km`
                    : `🌡️ ${sel.weatherTempC}°C • ${sel.weatherCondition}`}
              </p>
            </div>
            <button onClick={() => setSelected(null)} className="self-start rounded-full bg-white/10 p-1.5 text-slate-300">
              <X size={14} />
            </button>
          </div>
          {routeErr && <p className="mt-2 text-[11px] text-red-300">Yo'l topilmadi — GPS yoki internetni tekshiring</p>}
          <div className="mt-3 grid grid-cols-3 gap-2">
            <button
              onClick={() => openAr(sel.id)}
              className="flex items-center justify-center gap-1.5 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 py-2.5 text-xs font-bold text-white"
            >
              <ScanLine size={14} /> AR
            </button>
            <button
              onClick={buildRoute}
              disabled={routing}
              className="flex items-center justify-center gap-1.5 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 py-2.5 text-xs font-bold text-white disabled:opacity-60"
            >
              {routing ? <Loader2 size={14} className="animate-spin" /> : <Route size={14} />} {t('map_route')}
            </button>
            <a
              href={externalNavUrl(sel.coordinates[0], sel.coordinates[1])}
              target="_blank"
              rel="noreferrer"
              className="flex items-center justify-center gap-1.5 rounded-xl border border-white/15 bg-white/5 py-2.5 text-xs font-semibold text-slate-200"
            >
              <ExternalLink size={14} /> {t('map_navigate')}
            </a>
          </div>
        </div>
      )}
    </div>
  )
}

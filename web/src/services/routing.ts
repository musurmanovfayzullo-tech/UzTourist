// Driving directions via the public OSRM demo server (free, no API key).
export interface RouteResult {
  coords: [number, number][] // [lat, lon]
  distanceM: number
  durationS: number
}

export async function fetchRoute(
  from: { lat: number; lon: number },
  to: { lat: number; lon: number },
): Promise<RouteResult | null> {
  try {
    const url = `https://router.project-osrm.org/route/v1/driving/${from.lon},${from.lat};${to.lon},${to.lat}?overview=full&geometries=geojson`
    const ctrl = new AbortController()
    const timer = setTimeout(() => ctrl.abort(), 12000)
    const res = await fetch(url, { signal: ctrl.signal })
    clearTimeout(timer)
    if (!res.ok) return null
    const j = (await res.json()) as {
      code?: string
      routes?: { distance: number; duration: number; geometry: { coordinates: [number, number][] } }[]
    }
    const r = j.routes?.[0]
    if (j.code !== 'Ok' || !r) return null
    return {
      coords: r.geometry.coordinates.map(([lon, lat]) => [lat, lon] as [number, number]),
      distanceM: r.distance,
      durationS: r.duration,
    }
  } catch {
    return null
  }
}

export function formatDuration(s: number): string {
  const m = Math.round(s / 60)
  if (m < 60) return `${m} min`
  const h = Math.floor(m / 60)
  return `${h}h ${m % 60}m`
}

export function externalNavUrl(lat: number, lon: number): string {
  const ua = navigator.userAgent
  if (/iPhone|iPad|iPod/i.test(ua)) return `maps://?daddr=${lat},${lon}&dirflg=d`
  return `https://www.google.com/maps/dir/?api=1&destination=${lat},${lon}&travelmode=driving`
}

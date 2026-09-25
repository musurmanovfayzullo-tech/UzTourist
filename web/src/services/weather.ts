// Live weather via Open-Meteo (free, no API key). Cached per coordinate for 15 min.
export interface LiveWeather {
  tempC: number
  code: number
  condition: string
}

const TTL = 15 * 60 * 1000
const cache = new Map<string, { data: LiveWeather; at: number }>()

// WMO weather interpretation codes → short label
function describe(code: number): string {
  if (code === 0) return 'Clear sky'
  if (code <= 2) return 'Partly cloudy'
  if (code === 3) return 'Overcast'
  if (code <= 48) return 'Fog'
  if (code <= 57) return 'Drizzle'
  if (code <= 67) return 'Rain'
  if (code <= 77) return 'Snow'
  if (code <= 82) return 'Rain showers'
  if (code <= 86) return 'Snow showers'
  return 'Thunderstorm'
}

export async function fetchWeather(lat: number, lon: number): Promise<LiveWeather | null> {
  const key = `${lat.toFixed(2)},${lon.toFixed(2)}`
  const hit = cache.get(key)
  if (hit && Date.now() - hit.at < TTL) return hit.data
  try {
    const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,weather_code&timezone=Asia%2FTashkent`
    const res = await fetch(url)
    if (!res.ok) throw new Error('weather http ' + res.status)
    const j = (await res.json()) as { current?: { temperature_2m?: number; weather_code?: number } }
    const t = j.current?.temperature_2m
    const c = j.current?.weather_code ?? 0
    if (typeof t !== 'number') throw new Error('no temp')
    const data: LiveWeather = { tempC: Math.round(t), code: c, condition: describe(c) }
    cache.set(key, { data, at: Date.now() })
    return data
  } catch {
    return hit?.data ?? null
  }
}

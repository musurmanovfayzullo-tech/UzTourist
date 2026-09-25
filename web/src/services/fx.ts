// Live currency rates → UZS. Uses the free open.er-api.com endpoint (no API key).
// Falls back to bundled rates if the network call fails (offline / blocked).
import { CURRENCIES } from '../data/currency'

const FALLBACK: Record<string, number> = Object.fromEntries(
  CURRENCIES.map((c) => [c.code, c.rateToUzs]),
)

let cache: { rates: Record<string, number>; at: number } | null = null
const TTL = 10 * 60 * 1000 // 10 min

export async function fetchUzsRates(): Promise<Record<string, number>> {
  if (cache && Date.now() - cache.at < TTL) return cache.rates
  try {
    const res = await fetch('https://open.er-api.com/v6/latest/USD')
    const data = (await res.json()) as { result?: string; rates?: Record<string, number> }
    const r = data.rates
    if (!res.ok || !r || !r.UZS) throw new Error('bad fx response')
    // Convert each currency's USD-based rate into "how many UZS per 1 unit".
    const rates: Record<string, number> = { UZS: 1 }
    for (const c of CURRENCIES) {
      if (c.code === 'UZS') continue
      const perUsd = r[c.code]
      rates[c.code] = perUsd ? r.UZS / perUsd : FALLBACK[c.code]
    }
    cache = { rates, at: Date.now() }
    return rates
  } catch {
    return cache?.rates ?? FALLBACK
  }
}

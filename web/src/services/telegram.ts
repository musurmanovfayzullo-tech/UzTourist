// Telegram dispatch + admin PIN verification.
// Secrets come from build-time env vars (see .env / .env.example) — they are NOT
// hardcoded in source. NOTE: in a purely client-side bundle these values are still
// extractable; a serverless proxy (netlify/functions/secure.mjs) is provided for
// deployments that support Netlify Functions.
const BOT_TOKEN = (import.meta.env.VITE_TELEGRAM_BOT_TOKEN as string | undefined) ?? ''
const CHAT_ID = (import.meta.env.VITE_TELEGRAM_CHAT_ID as string | undefined) ?? ''
const ADMIN_PIN_HASH = (import.meta.env.VITE_ADMIN_PIN_HASH as string | undefined) ?? ''
const TG_API = `https://api.telegram.org/bot${BOT_TOKEN}`

async function tg(method: string, payload: Record<string, unknown>): Promise<boolean> {
  if (!BOT_TOKEN) return false
  try {
    const res = await fetch(`${TG_API}/${method}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    const data = (await res.json().catch(() => ({}))) as { ok?: boolean }
    return res.ok && data.ok === true
  } catch {
    return false
  }
}

export async function sendTelegramMessage(text: string, chatId?: string): Promise<boolean> {
  return tg('sendMessage', { chat_id: chatId || CHAT_ID, text: text.slice(0, 4000), parse_mode: 'HTML' })
}

export async function sendTelegramLocation(lat: number, lon: number, chatId?: string): Promise<boolean> {
  return tg('sendLocation', { chat_id: chatId || CHAT_ID, latitude: lat, longitude: lon })
}

export async function dispatchSosAlert(name: string, phone: string, lat: number | null, lon: number | null): Promise<boolean> {
  const text = [
    '🆘 <b>SOS SIGNAL — UzTourist Web</b>',
    `👤 Sayyoh: ${name}`,
    `📞 Tel: ${phone}`,
    lat != null && lon != null ? `📍 GPS: ${lat.toFixed(5)}, ${lon.toFixed(5)}` : '📍 GPS: aniqlanmadi',
    `🗺️ <a href="https://maps.google.com/?q=${lat ?? 0},${lon ?? 0}">Xaritada ko'rish</a>`,
    `🕐 ${new Date().toLocaleString('uz-UZ')}`,
  ].join('\n')
  const ok = await sendTelegramMessage(text)
  if (lat != null && lon != null) await sendTelegramLocation(lat, lon)
  return ok
}

export async function dispatchBookingAlert(title: string, lines: string[]): Promise<boolean> {
  const text = [`📋 <b>${title}</b>`, ...lines, `🕐 ${new Date().toLocaleString('uz-UZ')}`].join('\n')
  return sendTelegramMessage(text)
}

// Admin PIN is compared as a SHA-256 hash — the real PIN is never stored in the
// bundle, only its digest. Set VITE_ADMIN_PIN_HASH in .env.
async function sha256(text: string): Promise<string> {
  const buf = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(text))
  return Array.from(new Uint8Array(buf)).map((b) => b.toString(16).padStart(2, '0')).join('')
}

export async function verifyAdminPin(pin: string): Promise<boolean> {
  if (!ADMIN_PIN_HASH) return false
  try {
    return (await sha256(pin)) === ADMIN_PIN_HASH
  } catch {
    return false
  }
}

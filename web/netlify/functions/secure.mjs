// Server-side secrets live here as env vars — never bundled into the client.
// Set these in Netlify site env: TELEGRAM_BOT_TOKEN, TELEGRAM_DISPATCH_CHAT_ID, ADMIN_PIN
const BOT_TOKEN = process.env.TELEGRAM_BOT_TOKEN || ''
const CHAT_ID = process.env.TELEGRAM_DISPATCH_CHAT_ID || ''
const ADMIN_PIN = process.env.ADMIN_PIN || ''
const TG_API = `https://api.telegram.org/bot${BOT_TOKEN}`

const json = (status, obj) => ({
  statusCode: status,
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(obj),
})

async function tg(method, payload) {
  if (!BOT_TOKEN) return { ok: false, error: 'bot token not configured' }
  const res = await fetch(`${TG_API}/${method}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
  return res.json().catch(() => ({ ok: res.ok }))
}

export const handler = async (event) => {
  if (event.httpMethod !== 'POST') return json(405, { ok: false, error: 'POST only' })
  let body
  try {
    body = JSON.parse(event.body || '{}')
  } catch {
    return json(400, { ok: false, error: 'invalid json' })
  }
  const { action } = body

  // Verify admin PIN server-side — the real PIN never reaches the browser.
  if (action === 'verifyPin') {
    const ok = Boolean(ADMIN_PIN) && body.pin === ADMIN_PIN
    return json(200, { ok })
  }

  // Telegram dispatch — token stays server-side.
  if (action === 'sendMessage') {
    const r = await tg('sendMessage', {
      chat_id: body.chatId || CHAT_ID,
      text: String(body.text || '').slice(0, 4000),
      parse_mode: 'HTML',
    })
    return json(200, { ok: Boolean(r.ok) })
  }
  if (action === 'sendLocation') {
    const r = await tg('sendLocation', {
      chat_id: body.chatId || CHAT_ID,
      latitude: Number(body.lat),
      longitude: Number(body.lon),
    })
    return json(200, { ok: Boolean(r.ok) })
  }

  return json(400, { ok: false, error: 'unknown action' })
}

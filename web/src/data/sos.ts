// Secrets (Telegram bot token, admin PIN) are NOT stored here — they live
// server-side in Netlify env vars and are only reachable via /.netlify/functions/secure.
export const TELEGRAM_BOT_USERNAME = 'avtomaktab77bot'

export const SUPABASE_URL = 'https://izywwttrxuelprmlurya.supabase.co'
export const SUPABASE_ANON_KEY = 'sb_publishable_LjvCf48exrC0j8h0jFPeDA_GXnj7D59'

export interface EmergencyContact {
  id: string
  labelKey: string
  phone: string
  icon: string
  color: string
}

export const EMERGENCY_CONTACTS: EmergencyContact[] = [
  { id: 'ambulance', labelKey: 'sos_call_ambulance', phone: '103', icon: '🚑', color: '#EF4444' },
  { id: 'police', labelKey: 'sos_call_police', phone: '102', icon: '🚓', color: '#3B82F6' },
  { id: 'rescue', labelKey: 'sos_call_rescue', phone: '112', icon: '🛟', color: '#F97316' },
  { id: 'tourism_police', labelKey: 'sos_call_tourism_police', phone: '1173', icon: '🛡️', color: '#10B981' },
]

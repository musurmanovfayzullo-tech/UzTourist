import { createClient } from '@supabase/supabase-js'
import { SUPABASE_URL, SUPABASE_ANON_KEY } from '../data/sos'
import type { Destination } from '../data/destinations'

export const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY)

export interface RemoteMonument {
  id: string
  title: string
  city: string
  subtitle: string
  description: string
  history_fact: string
  unesco_year: number | null
  rating: number
  review_count: number
  category: string
  tag: string
  latitude: number
  longitude: number
  period: string
  image_url: string
  audio_url: string
  audio_script_uz: string
  is_verified?: boolean
}

export interface RemoteBooking {
  id?: number
  booking_code: string
  tour_id: number
  tour_title: string
  tourist_name: string
  tourist_phone: string
  tourist_email?: string
  tourist_country?: string
  start_date: string
  people_count: number
  total_price: number
  booking_status: string
  payment_status: string
  notes?: string
  created_at?: string
}

export interface NewBookingInput {
  tourTitle: string
  touristName: string
  touristPhone: string
  touristEmail?: string
  startDate: string
  peopleCount: number
  totalPrice: number
  bookingStatus?: string
  paymentStatus?: string
  notes?: string
}

export interface RemoteUser {
  full_name: string
  phone?: string
  email?: string
  language: string
  registered_at?: string
}

export interface AdminLog {
  action: string
  details: string
  created_at?: string
}

function mapCategory(cat: string): Destination['category'] {
  switch (cat.toUpperCase()) {
    case 'UNESCO': return 'UNESCO'
    case 'SACRED': return 'SACRED'
    case 'SILK_ROAD':
    case 'FORTRESS': return 'SILK_ROAD'
    case 'NATURE': return 'NATURE'
    case 'GASTRONOMY': return 'GASTRONOMY'
    default: return 'UNESCO'
  }
}

export function remoteToDestination(r: RemoteMonument): Destination {
  return {
    id: r.id,
    title: r.title,
    city: r.city,
    subtitle: r.subtitle || `Tarixiy Durdona (${r.city})`,
    description: r.description || "O'zbekistonning boy madaniy va me'moriy merosi.",
    historyFact: r.history_fact || `${r.title} ko'p asrlik boy tarixga ega.`,
    unescoYear: r.unesco_year,
    rating: r.rating,
    reviewCount: r.review_count,
    category: mapCategory(r.category),
    image: '/images/img_registan_1787819168326.jpg',
    tag: r.tag,
    coordinates: [r.latitude, r.longitude],
    distanceKmFromTashkent: 300,
    bestTimeToVisit: 'Ertalabki va kechki soatlar',
    highlights: ["Tarixiy Me'morchilik", 'Milliy Bezaklar', 'Sayyohlik Marshruti'],
    architecturalPeriod: r.period,
    crowdLevel: 'MODERATE',
    weatherTempC: 26,
    weatherCondition: 'Sunny & Clear',
    afrasiyobDuration: '2h 15m',
    remoteImageUrl: r.image_url || null,
    remoteAudioUrl: r.audio_url || null,
    remoteAudioScript: r.audio_script_uz || null,
    isFromSupabase: true,
  }
}

export async function fetchMonuments(): Promise<RemoteMonument[]> {
  const { data, error } = await supabase.from('monuments').select('*').order('id')
  if (error) throw error
  return (data ?? []) as RemoteMonument[]
}

export async function createBooking(b: NewBookingInput): Promise<boolean> {
  const row: Omit<RemoteBooking, 'id' | 'created_at'> = {
    booking_code: `UZB-${Date.now().toString(36).toUpperCase()}`,
    tour_id: 0,
    tour_title: b.tourTitle,
    tourist_name: b.touristName,
    tourist_phone: b.touristPhone,
    tourist_email: b.touristEmail ?? '',
    tourist_country: "O'zbekiston",
    start_date: b.startDate,
    people_count: b.peopleCount,
    total_price: b.totalPrice,
    booking_status: b.bookingStatus ?? 'Kutilmoqda',
    payment_status: b.paymentStatus ?? 'Kutilmoqda',
    notes: b.notes ?? '',
  }
  const { error } = await supabase.from('bookings').insert(row)
  return !error
}

export async function fetchBookings(): Promise<RemoteBooking[]> {
  const { data, error } = await supabase.from('bookings').select('*').order('created_at', { ascending: false })
  if (error) throw error
  return (data ?? []) as RemoteBooking[]
}

export async function registerUser(u: RemoteUser): Promise<boolean> {
  const { error } = await supabase.from('users').insert(u)
  return !error
}

export async function fetchUsers(): Promise<RemoteUser[]> {
  const { data, error } = await supabase.from('users').select('*').order('registered_at', { ascending: false })
  if (error) return []
  return (data ?? []) as RemoteUser[]
}

export async function logAdminAction(action: string, details: string): Promise<void> {
  await supabase.from('admin_logs').insert({ action, details })
}

export async function fetchAdminLogs(): Promise<AdminLog[]> {
  const { data, error } = await supabase.from('admin_logs').select('*').order('created_at', { ascending: false }).limit(50)
  if (error) return []
  return (data ?? []) as AdminLog[]
}

export function subscribeBookings(onChange: () => void) {
  return supabase
    .channel('bookings-changes')
    .on('postgres_changes', { event: '*', schema: 'public', table: 'bookings' }, onChange)
    .subscribe()
}

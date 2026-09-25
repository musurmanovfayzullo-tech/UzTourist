export type TransportType = 'FLIGHT' | 'TRAIN'

export const TRANSPORT_TYPES: Record<TransportType, { titleUz: string; icon: string }> = {
  FLIGHT: { titleUz: 'Aviabiletlar', icon: '✈️' },
  TRAIN: { titleUz: 'Poyezd biletlar', icon: '🚆' },
}

export interface TicketOffer {
  id: string
  transportType: TransportType
  carrierName: string
  serviceCode: string
  originCity: string
  destinationCity: string
  originStationOrAirport: string
  destinationStationOrAirport: string
  departureTime: string
  arrivalTime: string
  duration: string
  travelClass: string
  priceUzs: number
  availableSeats: number
  vehicleModel: string
  amenities: string[]
  rating: number
  isFastest: boolean
  isBestPrice: boolean
}

export interface TicketBookingOrder {
  orderId: string
  ticketOfferId: string
  passengerFirstName: string
  passengerLastName: string
  docType: string
  docNumber: string
  passengerPhone: string
  passengerEmail: string
  passengerCount: number
  seatNumber: string
  travelDate: string
  totalPriceUzs: number
  paymentMethod: string
  bookingStatus: string
  pnrCode: string
  qrCodePayload: string
  createdTimestamp: number
}

export const TICKET_CITIES = [
  'Toshkent',
  'Samarqand',
  'Buxoro',
  'Urganch / Xiva',
  'Nukus',
  "Farg'ona",
  'Termiz',
  'Navoiy',
]

export const FLIGHT_OFFERS: TicketOffer[] = [
  {
    id: 'avia_tas_skd_01', transportType: 'FLIGHT', carrierName: 'Uzbekistan Airways', serviceCode: 'HY-051',
    originCity: 'Toshkent', destinationCity: 'Samarqand',
    originStationOrAirport: 'Toshkent Janubiy (TAS Terminal 3)', destinationStationOrAirport: 'Samarqand Xalqaro Aeroporti (SKD)',
    departureTime: '07:30', arrivalTime: '08:25', duration: '55 daqiqa', travelClass: 'Ekonom',
    priceUzs: 295000, availableSeats: 18, vehicleModel: 'Airbus A320neo',
    amenities: ["Qo'l yuki 8 kg", 'Yuk 23 kg', 'Choy & Ichimlik', 'Elektron chipta'],
    rating: 4.9, isFastest: true, isBestPrice: false,
  },
  {
    id: 'avia_tas_skd_02', transportType: 'FLIGHT', carrierName: 'Silk Avia', serviceCode: 'US-115',
    originCity: 'Toshkent', destinationCity: 'Samarqand',
    originStationOrAirport: 'Toshkent Janubiy (TAS)', destinationStationOrAirport: 'Samarqand Aeroporti (SKD)',
    departureTime: '14:15', arrivalTime: '15:15', duration: '1 soat', travelClass: 'Standart',
    priceUzs: 245000, availableSeats: 12, vehicleModel: 'ATR 72-600',
    amenities: ["Qo'l yuki 5 kg", 'Tezkor registratsiya', "Konfort o'rindiq"],
    rating: 4.8, isFastest: false, isBestPrice: true,
  },
  {
    id: 'avia_tas_bhk_01', transportType: 'FLIGHT', carrierName: 'Uzbekistan Airways', serviceCode: 'HY-023',
    originCity: 'Toshkent', destinationCity: 'Buxoro',
    originStationOrAirport: 'Islom Karimov nomidagi Toshkent XA (TAS)', destinationStationOrAirport: 'Buxoro Xalqaro Aeroporti (BHK)',
    departureTime: '09:00', arrivalTime: '10:10', duration: '1s 10d', travelClass: 'Ekonom',
    priceUzs: 340000, availableSeats: 24, vehicleModel: 'Boeing 787-8 Dreamliner',
    amenities: ["Qo'l yuki 8 kg", 'Yuk 23 kg', 'Issiq yegulik', 'Media ekran'],
    rating: 5.0, isFastest: true, isBestPrice: false,
  },
  {
    id: 'avia_tas_ugc_01', transportType: 'FLIGHT', carrierName: 'Qanot Sharq', serviceCode: 'HH-207',
    originCity: 'Toshkent', destinationCity: 'Urganch / Xiva',
    originStationOrAirport: 'Toshkent Xalqaro Aeroporti (TAS)', destinationStationOrAirport: 'Urganch Xalqaro Aeroporti (UGC)',
    departureTime: '11:40', arrivalTime: '13:10', duration: '1s 30d', travelClass: 'Ekonom',
    priceUzs: 420000, availableSeats: 15, vehicleModel: 'Airbus A321neo',
    amenities: ["Qo'l yuki 8 kg", 'Yuk 20 kg', 'Nonushta', 'Konditsioner'],
    rating: 4.8, isFastest: false, isBestPrice: true,
  },
  {
    id: 'avia_tas_nku_01', transportType: 'FLIGHT', carrierName: 'Uzbekistan Airways', serviceCode: 'HY-011',
    originCity: 'Toshkent', destinationCity: 'Nukus',
    originStationOrAirport: 'Toshkent Xalqaro Aeroporti (TAS)', destinationStationOrAirport: 'Nukus Aeroporti (NCU)',
    departureTime: '16:30', arrivalTime: '18:15', duration: '1s 45d', travelClass: 'Ekonom',
    priceUzs: 460000, availableSeats: 9, vehicleModel: 'Airbus A320',
    amenities: ["Qo'l yuki 8 kg", 'Yuk 23 kg', 'Kofe va sharbat', 'USB zaryad'],
    rating: 4.7, isFastest: false, isBestPrice: false,
  },
  {
    id: 'avia_skd_tas_01', transportType: 'FLIGHT', carrierName: 'Uzbekistan Airways', serviceCode: 'HY-052',
    originCity: 'Samarqand', destinationCity: 'Toshkent',
    originStationOrAirport: 'Samarqand XA (SKD)', destinationStationOrAirport: 'Toshkent XA (TAS)',
    departureTime: '19:40', arrivalTime: '20:35', duration: '55 daqiqa', travelClass: 'Ekonom',
    priceUzs: 295000, availableSeats: 22, vehicleModel: 'Airbus A320neo',
    amenities: ["Qo'l yuki 8 kg", 'Yuk 23 kg', 'Choy & Kofe'],
    rating: 4.9, isFastest: true, isBestPrice: false,
  },
  {
    id: 'avia_bhk_tas_01', transportType: 'FLIGHT', carrierName: 'Silk Avia', serviceCode: 'US-124',
    originCity: 'Buxoro', destinationCity: 'Toshkent',
    originStationOrAirport: 'Buxoro XA (BHK)', destinationStationOrAirport: 'Toshkent XA (TAS)',
    departureTime: '18:00', arrivalTime: '19:15', duration: '1s 15d', travelClass: 'Standart',
    priceUzs: 320000, availableSeats: 14, vehicleModel: 'ATR 72-600',
    amenities: ["Qo'l yuki 5 kg", 'Tezkor parvoz'],
    rating: 4.8, isFastest: false, isBestPrice: false,
  },
]

export const TRAIN_OFFERS: TicketOffer[] = [
  {
    id: 'train_tas_skd_afro_01', transportType: 'TRAIN', carrierName: 'Afrosiyob Tezyurar Ekspress', serviceCode: 'AFROSIYOB #762',
    originCity: 'Toshkent', destinationCity: 'Samarqand',
    originStationOrAirport: 'Toshkent Markaziy (Shimoliy Vokzal)', destinationStationOrAirport: 'Samarqand Vokzali',
    departureTime: '08:00', arrivalTime: '10:13', duration: '2s 13d (250 km/h)', travelClass: 'Ekonom Klass',
    priceUzs: 185000, availableSeats: 38, vehicleModel: 'Talgo 250 (Ispaniya)',
    amenities: ['Konditsioner', 'Rozetka 220V', 'Bistro vagon', 'Quloqchin & Audio'],
    rating: 5.0, isFastest: true, isBestPrice: false,
  },
  {
    id: 'train_tas_skd_afro_vip', transportType: 'TRAIN', carrierName: 'Afrosiyob Tezyurar Ekspress', serviceCode: 'AFROSIYOB #762-VIP',
    originCity: 'Toshkent', destinationCity: 'Samarqand',
    originStationOrAirport: 'Toshkent Markaziy Vokzali', destinationStationOrAirport: 'Samarqand Vokzali',
    departureTime: '08:00', arrivalTime: '10:13', duration: '2s 13d (250 km/h)', travelClass: 'VIP Lyuks',
    priceUzs: 360000, availableSeats: 6, vehicleModel: 'Talgo 250 VIP Salon',
    amenities: ["Charm keng o'rindiqlar", 'Issiq nonushta & Kofe', 'Shaxsiy xizmatchi', 'Wi-Fi'],
    rating: 5.0, isFastest: false, isBestPrice: false,
  },
  {
    id: 'train_tas_bhk_afro_01', transportType: 'TRAIN', carrierName: 'Afrosiyob Tezyurar Ekspress', serviceCode: 'AFROSIYOB #764',
    originCity: 'Toshkent', destinationCity: 'Buxoro',
    originStationOrAirport: 'Toshkent Markaziy Vokzali', destinationStationOrAirport: 'Buxoro-1 (Kogon Vokzali)',
    departureTime: '08:30', arrivalTime: '12:28', duration: '3s 58d (250 km/h)', travelClass: 'Ekonom Klass',
    priceUzs: 240000, availableSeats: 28, vehicleModel: 'Talgo 250 (Ispaniya)',
    amenities: ['Konditsioner', 'Bistro vagon', 'Elektron chipta', "Qulay o'rindiq"],
    rating: 4.9, isFastest: true, isBestPrice: false,
  },
  {
    id: 'train_tas_skd_sharq', transportType: 'TRAIN', carrierName: 'Sharq Tezyurar Poyezdi', serviceCode: 'SHARQ #010',
    originCity: 'Toshkent', destinationCity: 'Samarqand',
    originStationOrAirport: 'Toshkent Janubiy Vokzali', destinationStationOrAirport: 'Samarqand Vokzali',
    departureTime: '09:15', arrivalTime: '12:35', duration: '3s 20d', travelClass: '1-Klass Kupe',
    priceUzs: 125000, availableSeats: 45, vehicleModel: 'Tezyurar Lokomotiv',
    amenities: ['Choyxona xizmati', 'Stol & Rozetka', 'Keng bagaj joyi'],
    rating: 4.7, isFastest: false, isBestPrice: true,
  },
  {
    id: 'train_tas_ugc_express', transportType: 'TRAIN', carrierName: "O'zbekiston Temir Yo'llari", serviceCode: 'KHIVA EXPRESS #056',
    originCity: 'Toshkent', destinationCity: 'Urganch / Xiva',
    originStationOrAirport: 'Toshkent Janubiy Vokzali', destinationStationOrAirport: 'Xiva Tarixiy Vokzali',
    departureTime: '21:00', arrivalTime: '10:45', duration: '13s 45d (Tungi reys)', travelClass: 'Kupe Yotoqli',
    priceUzs: 285000, availableSeats: 19, vehicleModel: 'Modern Yotoq Vagoni',
    amenities: ["Yotoq to'plami (ko'rpa-yostiq)", 'Choy & Qandolat', 'Konditsioner', 'Dush xonasi'],
    rating: 4.8, isFastest: false, isBestPrice: false,
  },
  {
    id: 'train_skd_tas_afro', transportType: 'TRAIN', carrierName: 'Afrosiyob Tezyurar Ekspress', serviceCode: 'AFROSIYOB #761',
    originCity: 'Samarqand', destinationCity: 'Toshkent',
    originStationOrAirport: 'Samarqand Vokzali', destinationStationOrAirport: 'Toshkent Markaziy Vokzali',
    departureTime: '17:30', arrivalTime: '19:43', duration: '2s 13d', travelClass: 'Ekonom Klass',
    priceUzs: 185000, availableSeats: 32, vehicleModel: 'Talgo 250',
    amenities: ['Konditsioner', 'Rozetka', 'Bistro vagon'],
    rating: 5.0, isFastest: true, isBestPrice: false,
  },
]

export const ALL_TICKET_OFFERS = [...FLIGHT_OFFERS, ...TRAIN_OFFERS]

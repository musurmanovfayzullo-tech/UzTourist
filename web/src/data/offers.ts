export type ServiceType = 'ALL' | 'TAXI' | 'DINING' | 'LODGING'

export const SERVICE_TYPES: Record<ServiceType, { title: string; icon: string; subtitle: string }> = {
  ALL: { title: 'Barchasi', icon: '✨', subtitle: 'Barcha maxsus xizmatlar' },
  TAXI: { title: 'Shaxsiy Avtopark', icon: '🚖', subtitle: 'VIP mashinalar & shaxsiy transfer' },
  DINING: { title: 'Milliy Taomlar', icon: '🍽️', subtitle: 'Choyxonalar & 100% Halol restoranlar' },
  LODGING: { title: 'Mehmonxonalar', icon: '🏨', subtitle: 'Karvonsaroylar & shohona xonalar' },
}

export interface TaxiOffer {
  id: string
  title: string
  tariff: string
  description: string
  priceEstimateUzs: string
  carModel: string
  passengerSeats: number
  luggageCapacity: number
  etaMinutes: number
  rating: number
  features: string[]
  phoneNumber: string
  dispatcherName: string
  destinationLat: number
  destinationLon: number
  targetCity: string
  badge: string
  isVipFleet: boolean
}

export interface DiningOffer {
  id: string
  name: string
  city: string
  cuisineType: string
  description: string
  priceRangeUzs: string
  rating: number
  reviewCount: number
  isHalal: boolean
  openingHours: string
  address: string
  phoneNumber: string
  image: string
  signatureDishes: string[]
  latitude: number
  longitude: number
  specialOffer: string
}

export interface LodgingOffer {
  id: string
  name: string
  city: string
  stars: number
  lodgingType: string
  description: string
  pricePerNightUzs: string
  rating: number
  reviewCount: number
  address: string
  amenities: string[]
  phoneNumber: string
  image: string
  roomType: string
  latitude: number
  longitude: number
  bonusPerk: string
}

export const TAXI_OFFERS: TaxiOffer[] = [
  {
    id: 'fleet_vip_business',
    title: 'VIP Business Class • Shaxsiy Park',
    tariff: 'VIP Business',
    description: 'Sohibqiron darajasidagi qulaylik. Yangi Kia K5 va Malibu 2 Premier avtomobillari, bepul sovuq ichimliklar, Wi-Fi va ingliz/rus tilli tajribali shaxsiy haydovchi.',
    priceEstimateUzs: '65,000 UZS / soat (shahar ichi)',
    carModel: 'Kia K5 / Chevrolet Malibu 2 Premier 2024',
    passengerSeats: 4, luggageCapacity: 3, etaMinutes: 5, rating: 4.99,
    features: ['Muzdek suv & Wi-Fi', 'Ingliz tili gid-haydovchi', 'Konditsioner iqlim nazorati', 'Telefon quvvatlagich'],
    phoneNumber: '+998910330460', dispatcherName: 'Dispetcher Sardor (VIP Liniya)',
    destinationLat: 39.6548, destinationLon: 66.9757,
    targetCity: 'Samarqand & Toshkent', badge: 'PREMIUM VIP', isVipFleet: true,
  },
  {
    id: 'fleet_comfort_plus',
    title: 'Premium Comfort+ • Tezkor Sayyohlik',
    tariff: 'Comfort+',
    description: "Shahar bo'ylab Registon, Shohi Zinda, Minorai Kalon va bozorlarga arzon va juda qulay shaxsiy avtopark taksisi.",
    priceEstimateUzs: '25,000 - 45,000 UZS (belgilangan manzilga)',
    carModel: 'Chevrolet Onix Turbo / Cobalt 2024',
    passengerSeats: 4, luggageCapacity: 2, etaMinutes: 3, rating: 4.94,
    features: ['Konditsioner', 'Toza salat', 'Tez yetib kelish', 'Naqd yoki Payme/Click'],
    phoneNumber: '+998781507777', dispatcherName: 'Markaziy Dispetcherlik',
    destinationLat: 39.6548, destinationLon: 66.9757,
    targetCity: 'Samarqand, Buxoro, Toshkent', badge: 'ENG TALABGIR', isVipFleet: false,
  },
  {
    id: 'fleet_tourist_minivan',
    title: "Silk Road Group Minivan • 8 O'rindiqli",
    tariff: 'Tourist Minivan',
    description: 'Oila va sayyohlik guruhlari uchun maxsus. Katta sig\'imli Hyundai Staria / H-1, barcha chamadonlar uchun ulkan yukxona va panoramali oynalar.',
    priceEstimateUzs: '180,000 UZS / transfer (aeroport/vokzal)',
    carModel: 'Hyundai Staria VIP / H-1 Grand Minivan',
    passengerSeats: 8, luggageCapacity: 8, etaMinutes: 10, rating: 4.98,
    features: ["8 ta qulay o'rindiq", 'Katta yukxona', 'Mikrofonli gid imkoniyati', 'Muzlatgich'],
    phoneNumber: '+998910330460', dispatcherName: "Minivan Transfer Bo'limi",
    destinationLat: 41.3783, destinationLon: 60.3594,
    targetCity: "Barcha Shaharlar (O'zbekiston bo'ylab)", badge: 'GURUH & OILA', isVipFleet: true,
  },
  {
    id: 'fleet_intercity_express',
    title: 'Shaharlararo VIP Express • Toshkent ➔ Samarqand ➔ Buxoro',
    tariff: 'Intercity VIP',
    description: "Shaharlararo eshikdan-eshikkacha to'g'ridan-to'g'ri qulay transfer. Magistral yo'llarda tez va xavfsiz harakatlanish.",
    priceEstimateUzs: "550,000 UZS / to'liq avtomobil",
    carModel: 'Chevrolet Traverse / Toyota Camry 75',
    passengerSeats: 4, luggageCapacity: 4, etaMinutes: 15, rating: 4.97,
    features: ["Magistral tajribali haydovchi", "Yo'lovchi sug'urtasi", "Ixtiyoriy to'xtashlar (Somsa & Choy)", 'Tezyurar marshrut'],
    phoneNumber: '+998998881122', dispatcherName: 'Shaharlararo Ekspress Dispetcher',
    destinationLat: 39.7758, destinationLon: 64.4158,
    targetCity: 'Toshkent - Samarqand - Buxoro - Xiva', badge: 'SHAHARLARARO', isVipFleet: true,
  },
  {
    id: 'fleet_airport_meet_greet',
    title: 'Aeroport & Vokzal VIP Tablichka Kutib Olish',
    tariff: 'Meet & Greet',
    description: 'Samolyot yoki poyezddan tushganingizda haydovchimiz ism-familiyangiz yozilgan lavha (tablichka) bilan kutib oladi va yuklaringizni ko\'tarib mehmonxonagacha eltadi.',
    priceEstimateUzs: '120,000 UZS / kutib olish',
    carModel: 'Kia K5 / Onix VIP Fleet',
    passengerSeats: 4, luggageCapacity: 3, etaMinutes: 5, rating: 4.99,
    features: ['Tablichka bilan kutib olish', 'Parvoz kechikishini bepul kutish', 'Yuklarga yordam', 'Mehmonxona resepshnigacha'],
    phoneNumber: '+998910330460', dispatcherName: 'VIP Kutib Olish Xizmati',
    destinationLat: 41.258, destinationLon: 69.281,
    targetCity: 'Toshkent, Samarqand, Urganch, Buxoro Aeroportlari', badge: 'KUTIB OLISH', isVipFleet: true,
  },
]

export const DINING_OFFERS: DiningOffer[] = [
  {
    id: 'samarkand_osh_center',
    name: 'Muborak Samarqand Oshi Markazi',
    city: 'Samarqand',
    cuisineType: "An'anaviy Samarqand Oshi",
    description: "Samarqandning mashhur qavat-qavat tayyorlanadigan sariq sabzili, mayizli va qo'zichoq go'shtli to'y oshi. Dasturxonda yangi pishgan non va shakarob.",
    priceRangeUzs: '45,000 - 90,000 UZS', rating: 4.97, reviewCount: 3120,
    isHalal: true, openingHours: '11:00 - 15:00 (Osh vaqti)',
    address: "Dahbed ko'chasi 24, Samarqand", phoneNumber: '+998662334455',
    image: '/images/uzbek_cuisine_plov_feast_1787820877737.jpg',
    signatureDishes: ['Samarqand Oshi', "Bedana go'shtli palov", 'Achchiq-chuchuk', "Ko'k choy"],
    latitude: 39.658, longitude: 66.969,
    specialOffer: "Sayyohlar uchun bepul ko'k choy va novvot",
  },
  {
    id: 'bukhara_labi_hovuz_choyxona',
    name: 'Choyxona Lab-i Hovuz & Milliy Taomlar',
    city: 'Buxoro',
    cuisineType: 'Buxoro Milliy Taomlari & Kabob',
    description: "Asrlar osha qad rostlagan tut daraxtlari soyasida, hovuz bo'yida shinam supa va eng shirin tandir kaboblar.",
    priceRangeUzs: '60,000 - 140,000 UZS', rating: 4.91, reviewCount: 2450,
    isHalal: true, openingHours: '09:00 - 23:00',
    address: 'B. Naqshband ko\'chasi, Lab-i Hovuz majmuasi', phoneNumber: '+998652241122',
    image: '/images/uzbek_cuisine_plov_feast_1787820877737.jpg',
    signatureDishes: ['Buxorocha tandir kabob', "Qovurma lag'mon", 'Qaymoqli somsa', "Za'faronli choy"],
    latitude: 39.7732, longitude: 64.4208,
    specialOffer: "Hovuz bo'yidagi eng yaxshi supalarga oldindan buyurtma",
  },
  {
    id: 'khiva_yasavulboshi_restaurant',
    name: 'Yasavulboshi Tarixiy Restorani',
    city: 'Xiva',
    cuisineType: "Xorazm O'ziga Xos Taomlari",
    description: 'Qadimiy madrasa ichidagi hashamatli milliy muhit. Xorazmning betakror shivit oshi va tuxum-barak taomlari.',
    priceRangeUzs: '50,000 - 120,000 UZS', rating: 4.93, reviewCount: 1890,
    isHalal: true, openingHours: '10:00 - 22:30',
    address: "Ichan Qal'a, Pahlavon Mahmud ko'chasi", phoneNumber: '+998622278899',
    image: '/images/uzbek_cuisine_plov_feast_1787820877737.jpg',
    signatureDishes: ["Shivit Oshi (Ko'k lag'mon)", 'Tuxum-barak', 'Xiva noni', 'Anor sharbati'],
    latitude: 41.3775, longitude: 60.3582,
    specialOffer: 'Xorazm milliy musiqasi va jonli dutor ijrosi',
  },
  {
    id: 'tashkent_chorsu_somsa',
    name: "Chorsu G'ishtin Tandir Somsa Markazi",
    city: 'Toshkent',
    cuisineType: "Tandir Somsa & Sho'rva",
    description: "Eski shahar Chorsu bozorining eng mashhur, qarsildoq varaqi tandir somsasi va no'xatli sho'rvasi.",
    priceRangeUzs: '20,000 - 55,000 UZS', rating: 4.89, reviewCount: 4200,
    isHalal: true, openingHours: '08:00 - 20:00',
    address: "Zarqaynar ko'chasi, Chorsu Bozor yonida", phoneNumber: '+998712440011',
    image: '/images/uzbek_cuisine_plov_feast_1787820877737.jpg',
    signatureDishes: ['Varaqi tandir somsa', 'Qozon kabob', 'Qatiqli mastava'],
    latitude: 41.3275, longitude: 69.2345,
    specialOffer: "Issiq tandirdan to'g'ridan-to'g'ri uzilgan somsalar",
  },
]

export const LODGING_OFFERS: LodgingOffer[] = [
  {
    id: 'registan_plaza_samarkand',
    name: 'Registan Silk Road Palace Hotel',
    city: 'Samarqand', stars: 5, lodgingType: 'Boutique Palace Hotel',
    description: 'Registon maydonidan 5 daqiqalik masofada, milliy naqshlar, shinam supa hovlisi va panoramic tom restorani.',
    pricePerNightUzs: '780,000 UZS / kecha', rating: 4.96, reviewCount: 1420,
    address: "Shoh Zinda ko'chasi 32, Samarqand",
    amenities: ['Bepul Wi-Fi', 'Shved stoli nonushta', 'Suzish havzasi', 'Shaxsiy park transferi', 'Konditsioner', 'SPA'],
    phoneNumber: '+998662359900',
    image: '/images/silk_road_hotel_courtyard_1787820893667.jpg',
    roomType: 'Deluxe King Room • Nonushta bilan',
    latitude: 39.6572, longitude: 66.979,
    bonusPerk: 'Registon maydoniga qaraydigan panoramali ayvon',
  },
  {
    id: 'bukhara_caravanserai_hotel',
    name: 'Kalyan Heritage Caravanserai',
    city: 'Buxoro', stars: 4, lodgingType: 'Tarixiy Karvonsaroy Mehmonxona',
    description: "19-asr madrasasi asosida qayta tiklangan, o'ymakor yog'och ustunli va Buxoro ganchkorlik san'ati bilan bezatilgan mehmonxona.",
    pricePerNightUzs: '550,000 UZS / kecha', rating: 4.94, reviewCount: 980,
    address: "Xo'ja Nuriddin ko'chasi 15, Buxoro Eski Shahar",
    amenities: ['Tarixiy hovli', 'Milliy nonushta', 'Wi-Fi', 'Shaxsiy shahar turi', 'Choyxona'],
    phoneNumber: '+998652237788',
    image: '/images/silk_road_hotel_courtyard_1787820893667.jpg',
    roomType: 'Silk Road Twin • Hovli ko\'rinishi',
    latitude: 39.7745, longitude: 64.417,
    bonusPerk: 'Qadimiy ganchkorlik xonalari va sharqona choyxona',
  },
  {
    id: 'khiva_ichan_kala_inn',
    name: "Ichan Qal'a Orient Star Inn",
    city: 'Xiva', stars: 4, lodgingType: 'Madrasa Mehmonxona',
    description: "Muhammad Aminxon madrasasi hujralarida joylashgan betakror tarixiy mehmonxona. Kalta Minor ro'parasida.",
    pricePerNightUzs: '620,000 UZS / kecha', rating: 4.92, reviewCount: 1150,
    address: "Ichan Qal'a, Ota Darvoza kirish joyi",
    amenities: ['Tarixiy hujralar', "Qal'a ichida joylashuv", 'Nonushta', 'Gid xizmati'],
    phoneNumber: '+998622285566',
    image: '/images/silk_road_hotel_courtyard_1787820893667.jpg',
    roomType: "Traditional Suite • Qadimiy ark ko'rinishi",
    latitude: 41.378, longitude: 60.359,
    bonusPerk: "To'g'ridan-to'g'ri Kalta Minor ro'parasida joylashgan",
  },
  {
    id: 'tashkent_silk_road_hostel',
    name: 'Silk Road Travelers Haven & Hostel',
    city: 'Toshkent', stars: 3, lodgingType: 'Zamonaviy Butik Xostel',
    description: "Sayyohlar uchun eng qulay va do'stona maskan. Metro va Amir Temur xiyoboniga juda yaqin.",
    pricePerNightUzs: '180,000 UZS / kecha', rating: 4.88, reviewCount: 2100,
    address: 'Navoiy shoh ko\'chasi 45, Toshkent',
    amenities: ['Yuqori tezlikdagi Wi-Fi', 'Oshxona', 'Kir yuvish', 'Kofe zona', '24/7 Qabulxona'],
    phoneNumber: '+998712330055',
    image: '/images/silk_road_hotel_courtyard_1787820893667.jpg',
    roomType: 'Private Single & Dormitory',
    latitude: 41.312, longitude: 69.28,
    bonusPerk: 'Metroga 3 daqiqalik masofa va qulay ish maydoni',
  },
]

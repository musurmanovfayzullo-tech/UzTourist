export type StaffRoleType = 'HISTORIAN_GUIDE' | 'VIP_DRIVER_ESCORT' | 'FULL_CONCIERGE' | 'PHOTO_GUIDE'

export const STAFF_ROLES: Record<StaffRoleType, { titleUz: string; subtitleUz: string; icon: string }> = {
  HISTORIAN_GUIDE: { titleUz: 'Tarixchi Shaxsiy Gid', subtitleUz: "Tarixiy obidalar, me'morchilik va chuqur ekskursiya", icon: '🎓' },
  VIP_DRIVER_ESCORT: { titleUz: 'VIP Haydovchi & Gid', subtitleUz: 'Premium avtomobil, qulay transfer va shahar sayohati', icon: '🚘' },
  FULL_CONCIERGE: { titleUz: "To'liq Sayohat Hamrohi", subtitleUz: 'Aeroportdan uchib ketguncha barcha xizmatlar kuratori', icon: '👑' },
  PHOTO_GUIDE: { titleUz: 'Foto-Gid & Media Hamroh', subtitleUz: 'Tarixiy obidalarda professional fotosessiya va ekskursiya', icon: '📸' },
}

export interface TourGuideStaff {
  id: string
  fullName: string
  roleType: StaffRoleType
  roleBadge: string
  experienceYears: number
  rating: number
  completedToursCount: number
  languages: string[]
  operatingCities: string[]
  specialties: string[]
  vehicleInfo?: string
  pricePerHourUzs: number
  pricePerDayUzs: number
  phoneNumber: string
  telegramUsername: string
  isAvailableToday: boolean
  avatarColorHex: string
  bioUz: string
  verifiedBadge: string
}

export type BookingOrderStatus = 'PENDING' | 'CONFIRMED' | 'ON_THE_WAY' | 'COMPLETED' | 'CANCELLED'

export const ORDER_STATUS: Record<BookingOrderStatus, { labelKey: string; color: string }> = {
  PENDING: { labelKey: 'order_pending', color: '#FFA000' },
  CONFIRMED: { labelKey: 'order_confirmed', color: '#00C853' },
  ON_THE_WAY: { labelKey: 'order_ontheway', color: '#00B0FF' },
  COMPLETED: { labelKey: 'order_completed', color: '#7C4DFF' },
  CANCELLED: { labelKey: 'order_cancelled', color: '#FF5252' },
}

export interface TourBookingOrder {
  orderId: string
  staffId: string
  staffName: string
  staffRole: StaffRoleType
  staffPhone: string
  touristName: string
  touristPhone: string
  selectedCity: string
  tourDate: string
  tourStartTime: string
  durationHours: number
  touristCount: number
  preferredLanguage: string
  pickupLocation: string
  includedExtras: string[]
  specialRequests: string
  totalAmountUzs: number
  status: BookingOrderStatus
  createdAtFormatted: string
}

export const SAMPLE_STAFF: TourGuideStaff[] = [
  {
    id: 'staff_bobur_01',
    fullName: 'Bobur Mirzayev',
    roleType: 'HISTORIAN_GUIDE',
    roleBadge: 'OLIY TOIFALI TARIXCHI GID',
    experienceYears: 9,
    rating: 4.99,
    completedToursCount: 480,
    languages: ["🇬🇧 Ingliz", '🇷🇺 Rus', "🇺🇿 O'zbek"],
    operatingCities: ['Samarqand', 'Buxoro', 'Shahrisabz'],
    specialties: ['Registon & Amir Temur davri', "Sharq me'morchiligi", 'Afrosiyob arxeologiyasi', "Madaniy an'analar"],
    pricePerHourUzs: 120000,
    pricePerDayUzs: 850000,
    phoneNumber: '+998907771234',
    telegramUsername: '@bobur_guide_uz',
    isAvailableToday: true,
    avatarColorHex: '#0047AB',
    bioUz: "O'zbekiston Turizm qo'mitasi litsenziyasiga ega oliy toifali tarixchi. Samarqand va Buxoro tarixini eng nozik tafsilotlarigacha qiziqarli hikoya qilib beradi.",
    verifiedBadge: "Litsenziyali Mas'ul Xodim",
  },
  {
    id: 'staff_sherzod_02',
    fullName: 'Sherzod Rahimov',
    roleType: 'VIP_DRIVER_ESCORT',
    roleBadge: 'VIP HAYDOVCHI & HAMROH',
    experienceYears: 12,
    rating: 4.97,
    completedToursCount: 620,
    languages: ['🇷🇺 Rus', '🇬🇧 Ingliz', "🇺🇿 O'zbek", '🇹🇷 Turk'],
    operatingCities: ['Toshkent', 'Samarqand', 'Buxoro', 'Zomin'],
    specialties: ['Aeroportda kutib olish', "Shahar bo'ylab VIP sayr", "Tog'li hududlar safari", 'Eng sara restoranlar'],
    vehicleInfo: 'Chevrolet Malibu 2 Premier 2024 (Konditsioner, Wi-Fi, Muzdek ichimliklar)',
    pricePerHourUzs: 150000,
    pricePerDayUzs: 1100000,
    phoneNumber: '+998918885678',
    telegramUsername: '@sherzod_vip_drive',
    isAvailableToday: true,
    avatarColorHex: '#00897B',
    bioUz: '12 yillik professional haydash tajribasi. Xorijiy delegatsiyalar va VIP mehmonlar uchun qulay, xavfsiz va maroqli sayohatni ta\'minlaydi.',
    verifiedBadge: "Litsenziyali Mas'ul Xodim",
  },
  {
    id: 'staff_nilufar_03',
    fullName: 'Nilufar Karimova',
    roleType: 'HISTORIAN_GUIDE',
    roleBadge: 'SAN\'AT VA HUNARMANDCHILIK GIDI',
    experienceYears: 7,
    rating: 4.98,
    completedToursCount: 390,
    languages: ['🇫🇷 Fransuz', '🇬🇧 Ingliz', "🇺🇿 O'zbek"],
    operatingCities: ['Buxoro', 'Xiva', 'Samarqand'],
    specialties: ["Ichan Qal'a sirlari", 'Ipak gilamchilik', "Zardo'zlik va kulolchilik", 'Xonliklar davri tarixi'],
    pricePerHourUzs: 130000,
    pricePerDayUzs: 900000,
    phoneNumber: '+998935559012',
    telegramUsername: '@nilufar_bukhara_guide',
    isAvailableToday: true,
    avatarColorHex: '#7B1FA2',
    bioUz: 'Buxoro va Xiva qadimiy obidalari bo\'yicha mutaxassis. Fransuz va ingliz sayyohlari bilan ko\'p yillik muvaffaqiyatli ish tajribasiga ega.',
    verifiedBadge: "Litsenziyali Mas'ul Xodim",
  },
  {
    id: 'staff_dilshod_04',
    fullName: 'Dilshodbek Mansurov',
    roleType: 'FULL_CONCIERGE',
    roleBadge: "TO'LIQ SAYOHAT KURATORI",
    experienceYears: 14,
    rating: 5.0,
    completedToursCount: 750,
    languages: ['🇬🇧 Ingliz', '🇩🇪 Nemis', '🇷🇺 Rus', '🇹🇷 Turk', "🇺🇿 O'zbek"],
    operatingCities: ['Barcha shaharlar', 'Toshkent', 'Samarqand', 'Buxoro', 'Xiva'],
    specialties: ['24/7 Shaxsiy Konsyerj', 'Navbatsiz VIP chiptalar', 'Eng yaxshi milliy choyxonalar', 'Xavfsizlik va transfer'],
    vehicleInfo: 'Mercedes Sprinter VIP / Minivan (Guruhlar uchun)',
    pricePerHourUzs: 200000,
    pricePerDayUzs: 1500000,
    phoneNumber: '+998901112233',
    telegramUsername: '@dilshod_concierge_uz',
    isAvailableToday: true,
    avatarColorHex: '#C2185B',
    bioUz: "Butun O'zbekiston bo'ylab 1 kundan 10 kungacha bo'lgan turlarni to'liq tashkillashtiruvchi bosh kurator. Mehmonxona, transport, ovqatlanish va ekskursiyalarni 100% o'z zimmasiga oladi.",
    verifiedBadge: "Litsenziyali Mas'ul Xodim",
  },
  {
    id: 'staff_aziz_05',
    fullName: 'Azizbek Qodirov',
    roleType: 'PHOTO_GUIDE',
    roleBadge: 'FOTO-GID & MEDIA HAMROH',
    experienceYears: 6,
    rating: 4.95,
    completedToursCount: 310,
    languages: ['🇬🇧 Ingliz', '🇷🇺 Rus', "🇺🇿 O'zbek"],
    operatingCities: ['Samarqand', 'Buxoro', 'Toshkent'],
    specialties: ['Sony Alpha 4K suratga olish', 'Eng chiroyli Instagram nuqtalar', 'Tarixiy kiyimlar ijarasi', 'Kvadrokopter aerotushirish'],
    pricePerHourUzs: 140000,
    pricePerDayUzs: 950000,
    phoneNumber: '+998946663344',
    telegramUsername: '@aziz_photoguide',
    isAvailableToday: true,
    avatarColorHex: '#E65100',
    bioUz: "Tarixiy obidalar oldida unutilmas professional fotosuratlar va roliklar tayyorlovchi g'oyat iqtidorli foto-gid.",
    verifiedBadge: "Litsenziyali Mas'ul Xodim",
  },
]

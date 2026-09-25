export type PaymentMethod = 'CARD' | 'PAYME' | 'CLICK' | 'CASH'

export const PAYMENT_METHODS: Record<PaymentMethod, { title: string; icon: string; desc: string }> = {
  CARD: { title: "Bank kartasi", icon: '💳', desc: 'Uzcard, Humo, Visa, Mastercard' },
  PAYME: { title: 'Payme', icon: '🟢', desc: "Payme hamyon orqali to'lov" },
  CLICK: { title: 'Click', icon: '🔵', desc: "Click evro orqali to'lov" },
  CASH: { title: "Naqd pul", icon: '💵', desc: "Xodimga yoki yetib kelganda to'lash" },
}

export interface TariffPlace {
  name: string
  description: string
  icon: string
  highlights: string[]
}

export interface ExtraExpense {
  title: string
  category: string
  amountUsd: number
  date: string
  receiptNumber: string
  isIncludedInTariff: boolean
}

export interface TourTariff {
  id: string
  tariffNumber: number
  name: string
  titleBadge: string
  priceUsd: number
  durationDays: number
  description: string
  coverEmoji: string
  hotelIncludedDesc: string
  foodIncludedDesc: string
  transportIncludedDesc: string
  tourGuideIncludedDesc: string
  placesToVisit: TariffPlace[]
  includedFeatures: string[]
  extraExpensesEstimate: ExtraExpense[]
  isPopular: boolean
  isVip: boolean
}

export interface TariffBookingReceipt {
  orderId: string
  tariffId: string
  tariffName: string
  priceUsd: number
  touristName: string
  touristPhone: string
  startDate: string
  guestsCount: number
  paymentMethod: PaymentMethod
  cardNumberMasked?: string
  cardType?: string
  totalAmountUsd: number
  timestamp: number
  paymentStatus: string
  qrCodePayload: string
  extraExpenses: ExtraExpense[]
  note: string
}

export const USD_TO_UZS = 12600

export function formatUsd(v: number): string {
  return `$${Math.round(v).toLocaleString('en-US')}`
}
export function formatUzs(v: number): string {
  return `${Math.round(v).toLocaleString('en-US')} UZS`
}

export const SAMPLE_TARIFFS: TourTariff[] = [
  {
    id: 'tariff_1_samarkand_2days',
    tariffNumber: 1,
    name: '1-Tarif: Samarqand Klassik Tur (2 Kun)',
    titleBadge: 'ENG MASHHUR VA SEVIMLI',
    priceUsd: 500,
    durationDays: 2,
    description:
      "Samarqandning eng go'zal 5 ta tarixiy va zamonaviy maskanini 2 kun davomida to'liq ziyorat qilish, 3 mahal milliy oshxona, qulay mehmonxona va shaxsiy taksi xizmati.",
    coverEmoji: '🕌',
    hotelIncludedDesc: '2 kechalik qulay 4-yulduzli mehmonxona (nonushtasi bilan)',
    foodIncludedDesc:
      '3 mahal milliy taomlar: mashhur Samarqand oshi, tandir kabob, somsa va choyxona mehmondorchiligi',
    transportIncludedDesc:
      "Shaxsiy qulay Taxi / Haydovchi (barcha yo'nalishlar, vokzal/aeroportdan kutib olish va kuzatish)",
    tourGuideIncludedDesc: 'Samarqandlik tajribali sertifikatlangan shaxsiy gid',
    placesToVisit: [
      {
        name: '1. Registon Maydoni',
        description: "Sharq durdonasi — Ulug'bek, Tillakori va Sherdor madrasalari ansambli",
        icon: '🕌',
        highlights: ['Tarixiy madrasalar', 'Kechki chiroqlar shousi', 'Tillakori oltin gumbazi'],
      },
      {
        name: "2. Go'ri Amir Maqbarasi",
        description: 'Buyuk Sohibqiron Amir Temur va Temuriylar sulolasi muazzam maqbarasi',
        icon: '👑',
        highlights: ['Nefrit qabrtosh', 'Moviy gumbazlar', 'Temuriylar tarixi'],
      },
      {
        name: '3. Shohi Zinda Majmuasi',
        description: 'Nafis moviy koshinlar, zinalar va Qusam ibn Abbos ziyoratgohi',
        icon: '🏛️',
        highlights: ['Muqaddas feruza koshinlar', "40 pog'onali zinalar", "Sharqona me'morchilik"],
      },
      {
        name: '4. Islom Karimov Qabri & Maqbarasi',
        description: "O'zbekistonning Birinchi Prezidenti Islom Karimov maqbarasi (Hazrati Xizr majmuasi)",
        icon: '🌸',
        highlights: ['Hazrati Xizr masjidi', "Go'zal shahar panoramasi", 'Milliy marmar naqshlar'],
      },
      {
        name: "5. Mirzo Ulug'bek Rasadxonasi & Silk Road Park",
        description: "XV asr astronomik mo'jizasi hamda 'Boqiy Shahar' zamonaviy etno-parki",
        icon: '🔭',
        highlights: ['Sekstant asbobi', 'Boqiy Shahar majmuasi', 'Favvoralar va kechki sayr'],
      },
    ],
    includedFeatures: [
      '🏨 4-yulduzli qulay mehmonxona (2 kecha)',
      '🍲 3 mahal mazali milliy ovqatlanish (Samarqand oshi, kabob)',
      "🚕 Shaxsiy Taxi / Transfer (2 kun to'liq siz bilan)",
      '🎫 5 ta barcha tarixiy obidalarga kirish chiptalari',
      "🧾 Barcha xarajatlar cheklari ilovada ko'rinadi",
      "💳 Karta yoki Naqd to'lov imkoniyati",
    ],
    extraExpensesEstimate: [
      { title: 'Mehmonxona (2 kecha, nonushta bilan)', category: 'Hotel', amountUsd: 160, date: '1-2 kun', receiptNumber: 'CHK-HTL-8841', isIncludedInTariff: true },
      { title: '3 mahal milliy taomlar (Samarqand Oshi, Go\'sht)', category: 'Taom', amountUsd: 120, date: '1-2 kun', receiptNumber: 'CHK-REST-3920', isIncludedInTariff: true },
      { title: 'Shaxsiy Taxi & Transfer (2 kun cheklovsiz)', category: 'Transport', amountUsd: 110, date: '1-2 kun', receiptNumber: 'CHK-TAXI-5512', isIncludedInTariff: true },
      { title: '5 ta obidaga kirish chiptalari & Gid xizmati', category: 'Chipta/Gid', amountUsd: 80, date: '1-2 kun', receiptNumber: 'CHK-TKT-1049', isIncludedInTariff: true },
      { title: 'Milliy suvenirlar va shirinliklar zaxirasi', category: "Qo'shimcha", amountUsd: 30, date: '2-kun', receiptNumber: 'CHK-EXTRA-9901', isIncludedInTariff: true },
    ],
    isPopular: true,
    isVip: false,
  },
  {
    id: 'tariff_2_vip_7days',
    tariffNumber: 2,
    name: "2-Tarif: 1500$ VIP Ipak Yo'li Turi (7 Kun)",
    titleBadge: 'PREMIUM VIP TANLOV',
    priceUsd: 1500,
    durationDays: 7,
    description:
      "7 kunlik qirolona sayohat: 5-yulduzli VIP mehmonxonalar, eng elita restoranlarda 3 mahal taomlar, VIP shaxsiy avtomobil va Toshkent-Samarqand-Buxoro bo'ylab unutilmas tajriba.",
    coverEmoji: '💎',
    hotelIncludedDesc: '7 kechalik 5-yulduzli lyuks mehmonxonalar (Hilton, Silk Road Samarkand, Sahid Zarafshan)',
    foodIncludedDesc: '3 mahal A-la-carte va premium restoranlarda eng sara taomlar, shirinliklar, salqin ichimliklar',
    transportIncludedDesc: 'VIP Shaxsiy Avtomobil (Kia K5 / Malibu 2 Premier 2024), tezyurar Afrosiyob VIP poyezd chiptalari',
    tourGuideIncludedDesc: "Shaxsiy VIP tarixchi va ko'p tilli professional gid",
    placesToVisit: [
      {
        name: 'Toshkent: Tashkent City & Magic City',
        description: "Zamonaviy poytaxt ko'rki, musiqiy favvoralar, Amir Temur xiyoboni",
        icon: '🌆',
        highlights: ['Tashkent City Mall', 'Magic City', 'Chorsu bozori'],
      },
      {
        name: "Samarqand: Registon, Go'ri Amir, Boqiy Shahar",
        description: 'Temuriylar poytaxtining barcha 5 ta asosiy maskani va Silk Road Samarkand majmuasi',
        icon: '🕌',
        highlights: ['Registon maydoni', 'Shohi Zinda', 'Boqiy Shahar VIP'],
      },
      {
        name: "Buxoro: Minorai Kalon, Ark Qal'asi, Labi Hovuz",
        description: "Buxoroi Sharif qadimiy ko'chalari, karvonsaroylar va saroylar",
        icon: '✨',
        highlights: ["Ark qal'asi", 'Poi Kalon', 'Sitorai Mohi Xosa'],
      },
      {
        name: "Amirsoy Tog' Kurorti (Qo'shimcha Relaks)",
        description: "Chorvoq va Amirsoy tog'larida dor yo'li sayri va toza havo",
        icon: '🏔️',
        highlights: ["Amirsoy dor yo'li", "Tog' manzaralari", 'Premium relaks'],
      },
    ],
    includedFeatures: [
      '🌟 7 kecha 5-yulduzli lyuks mehmonxonalar',
      '🍽️ 3 mahal premium VIP restoranlarda taomlanish',
      '🚘 Shaxsiy VIP avtopark & shaxsiy haydovchi (7 kun)',
      '🚅 Afrosiyob VIP tezyurar poyezd chiptalari',
      '👑 Shaxsiy VIP Gid va tarjimon',
      '🧾 Ilovada barcha cheklar va xarajatlar aniq hisobi',
      "💳 Karta (Visa/Master/Humo/Uzcard) yoki Naqd to'lov",
    ],
    extraExpensesEstimate: [
      { title: '5-Yulduzli VIP Hotel (7 kecha, All-Inclusive)', category: 'Hotel', amountUsd: 650, date: '1-7 kun', receiptNumber: 'CHK-VIP-HTL-771', isIncludedInTariff: true },
      { title: '3 mahal VIP Restoran & A-la-carte taomlar', category: 'Taom', amountUsd: 380, date: '1-7 kun', receiptNumber: 'CHK-VIP-REST-410', isIncludedInTariff: true },
      { title: 'Shaxsiy VIP Avtomobil + Afrosiyob VIP poyezd', category: 'Transport', amountUsd: 320, date: '1-7 kun', receiptNumber: 'CHK-VIP-AUTO-992', isIncludedInTariff: true },
      { title: 'VIP Gid, Teatr va Barcha muzey chiptalari', category: 'Chipta/Gid', amountUsd: 150, date: '1-7 kun', receiptNumber: 'CHK-VIP-TKT-301', isIncludedInTariff: true },
    ],
    isPopular: false,
    isVip: true,
  },
  {
    id: 'tariff_3_uzbekistan_grand_14days',
    tariffNumber: 3,
    name: "3-Tarif: 4500$ Butun O'zbekiston Bo'ylab Grand Tur",
    titleBadge: "ENG TO'LIQ VA MUKAMMAL GRAND TUR",
    priceUsd: 4500,
    durationDays: 14,
    description:
      "14 kun davomida Butun O'zbekiston bo'ylab (Toshkent, Samarqand, Buxoro, Xiva, Shahrisabz, Zomin, Qoraqalpog'iston): barcha samolyot/poyezd chiptalari, eng oliy darajadagi hotellar, xarajatlar cheklari va to'liq xizmat.",
    coverEmoji: '🇺🇿',
    hotelIncludedDesc: '14 kechalik eng nufuzli 5-yulduzli & milliy Butik mehmonxonalar',
    foodIncludedDesc: '3 mahal xalqaro va milliy gourmet taomlanish (har bir viloyatning eng mashhur tansiq taomlari)',
    transportIncludedDesc: 'Shaxsiy biznes klass avtomobillar, ichki aviareyslar (Toshkent-Urganch-Nukus) va Afrosiyob VIP poyezdlar',
    tourGuideIncludedDesc: 'Professional akademik tarixchi va shaxsiy 24/7 konsyerj xizmati',
    placesToVisit: [
      {
        name: 'Toshkent & Amirsoy',
        description: 'Poytaxtning zamonaviy binolari, Amirsoy kurorti va Chorvoq suvlari',
        icon: '🏔️',
        highlights: ['Toshkent teleminorasi', 'Amirsoy Resort', 'Chorvoq'],
      },
      {
        name: 'Samarqand & Shahrisabz',
        description: "Registon, Go'ri Amir, Shohi Zinda, Islom Karimov qabri, Oqsaroy majmuasi",
        icon: '🕌',
        highlights: ['Registon 5 ta maskan', 'Oqsaroy', 'Silk Road Samarkand'],
      },
      {
        name: 'Buxoro & Karvonsaroylar',
        description: "Minorai Kalon, Ark qal'asi, Sitorai Mohi Xosa saroyi, qadimiy xammomlar",
        icon: '✨',
        highlights: ['Ark', 'Poi Kalon', 'Labi Hovuz'],
      },
      {
        name: "Xiva & Ichan Qal'a",
        description: "Ochiq osmon ostidagi muzey, Kalta Minor, Tosh Hovli, Juma masjidi",
        icon: '🏰',
        highlights: ["Ichan Qal'a", 'Kalta Minor', 'Xorazm lazzatlari'],
      },
      {
        name: "Zomin & Mo'ynoq (Orol Dengizi)",
        description: "O'zbekiston Shveytsariyasi tog'lari va Orol dengizi kemalar qabristoni",
        icon: '🌊',
        highlights: ["Zomin osma ko'prigi", 'Orol dengizi', 'Savitskiy muzeyi'],
      },
    ],
    includedFeatures: [
      "🇺🇿 14 kun davomida butun O'zbekiston bo'ylab to'liq tur",
      '🏨 5-yulduzli elita va milliy butik mehmonxonalar',
      '✈️ Ichki samolyot reyslari va Afrosiyob VIP poyezdlar',
      '🚘 Shaxsiy 24/7 biznes klass mashina va shaxsiy haydovchi',
      '🍲 3 mahal eng sara tansiq milliy va xalqaro taomlar',
      "🎫 Barcha muzeylar, qo'riqxonalar va tarixiy obidalarga kirish",
      "🧾 Har bir xarajat va to'lov ilovada cheklari bilan to'liq saqlanadi",
      "💳 Karta yoki Naqd pul bilan to'lash",
    ],
    extraExpensesEstimate: [
      { title: '5-Yulduzli va Butik Mehmonxonalar (14 kecha)', category: 'Hotel', amountUsd: 1850, date: '1-14 kun', receiptNumber: 'CHK-GRAND-HTL-1401', isIncludedInTariff: true },
      { title: '3 mahal tansiq milliy va xalqaro taomlanish', category: 'Taom', amountUsd: 1100, date: '1-14 kun', receiptNumber: 'CHK-GRAND-REST-552', isIncludedInTariff: true },
      { title: 'Ichki Aviareyslar + Afrosiyob VIP + Shaxsiy Mashina', category: 'Transport', amountUsd: 950, date: '1-14 kun', receiptNumber: 'CHK-GRAND-TRNS-803', isIncludedInTariff: true },
      { title: 'Shaxsiy Akademik Gid, Ruxsatnomalar & Barcha chiptalar', category: 'Chipta/Gid', amountUsd: 450, date: '1-14 kun', receiptNumber: 'CHK-GRAND-TKT-211', isIncludedInTariff: true },
      { title: "Suvenirlar, xalq hunarmandchiligi va maxsus sovg'alar", category: "Qo'shimcha", amountUsd: 150, date: '14-kun', receiptNumber: 'CHK-GRAND-SUV-901', isIncludedInTariff: true },
    ],
    isPopular: false,
    isVip: true,
  },
]

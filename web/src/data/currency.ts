export interface CurrencyRate {
  code: string
  name: string
  flag: string
  rateToUzs: number
  symbol: string
}

export interface ExpenseCategory {
  id: string
  title: string
  icon: string
  allocatedUzs: number
  spentUzs: number
}

export interface ExpenseItem {
  id: string
  title: string
  categoryId: string
  amountUzs: number
  timeAgo: string
  city: string
}

export const CURRENCIES: CurrencyRate[] = [
  { code: 'USD', name: 'AQSH Dollari', flag: '🇺🇸', rateToUzs: 12850, symbol: '$' },
  { code: 'EUR', name: 'Yevro', flag: '🇪🇺', rateToUzs: 13950, symbol: '€' },
  { code: 'RUB', name: 'Rossiya Rubli', flag: '🇷🇺', rateToUzs: 138.5, symbol: '₽' },
  { code: 'CNY', name: 'Xitoy Yuani', flag: '🇨🇳', rateToUzs: 1780, symbol: '¥' },
  { code: 'GBP', name: 'Angliya Funt Sterlingi', flag: '🇬🇧', rateToUzs: 16400, symbol: '£' },
  { code: 'TRY', name: 'Turk Lirasi', flag: '🇹🇷', rateToUzs: 380, symbol: '₺' },
  { code: 'JPY', name: 'Yaponiya Iyenasi', flag: '🇯🇵', rateToUzs: 85.5, symbol: '¥' },
  { code: 'KZT', name: 'Qozoq Tengesi', flag: '🇰🇿', rateToUzs: 27.5, symbol: '₸' },
  { code: 'UZS', name: "O'zbek So'mi", flag: '🇺🇿', rateToUzs: 1, symbol: "so'm" },
]

export const EXPENSE_CATEGORIES: ExpenseCategory[] = [
  { id: 'cat_transport', title: 'Shaxsiy Taxi & Poezd', icon: '🚖', allocatedUzs: 1500000, spentUzs: 0 },
  { id: 'cat_dining', title: 'Milliy Taomlar & Choy', icon: '🍽️', allocatedUzs: 2000000, spentUzs: 0 },
  { id: 'cat_hotel', title: 'Karvonsaroy & Mehmonxona', icon: '🏨', allocatedUzs: 4500000, spentUzs: 0 },
  { id: 'cat_tickets', title: 'Muzey & Obidalar', icon: '🎫', allocatedUzs: 800000, spentUzs: 0 },
  { id: 'cat_souvenirs', title: 'Hunarmandchilik & Bozor', icon: '🛍️', allocatedUzs: 1200000, spentUzs: 0 },
]

export const TIPPING_GUIDES: [string, string][] = [
  ['🍽️ Restoran va Choyxonalar', "Odatda hisob-kitobga 10-15% xizmat haqi qo'shiladi. Alohida xizmat yoqsa, 10,000 - 30,000 UZS qoldirish samimiy mehmondo'stlik belgisi hisoblanadi."],
  ['🚖 Shaxsiy Taxi Haydovchisi', 'Shaharlararo yoki shahar ichi xizmatda yuklarga yordam bersa, 10,000 - 20,000 UZS yoki hisobni yaxlitlash tavsiya etiladi.'],
  ['🏨 Mehmonxona Xizmatchisi', 'Chamadonlarni xonaga olib kirib bergan porterga 10,000 - 15,000 UZS.'],
  ['🏺 Mahalliy Tarixchi Gid', "Katta qiziqish bilan 2-3 soatlik ekskursiya o'tkazib bergan gidga 50,000 - 100,000 UZS minnatdorchilik bildiriladi."],
]

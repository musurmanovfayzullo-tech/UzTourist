import type { LangCode } from '../i18n'

export type DestinationCategory = 'ALL' | 'UNESCO' | 'SACRED' | 'SILK_ROAD' | 'NATURE' | 'GASTRONOMY'
export type CrowdLevel = 'LOW' | 'MODERATE' | 'HIGH'

export interface Destination {
  id: string
  title: string
  city: string
  subtitle: string
  description: string
  historyFact: string
  unescoYear: number | null
  rating: number
  reviewCount: number
  category: Exclude<DestinationCategory, 'ALL'>
  image: string
  tag: string
  coordinates: [number, number]
  distanceKmFromTashkent: number
  bestTimeToVisit: string
  highlights: string[]
  architecturalPeriod: string
  crowdLevel: CrowdLevel
  weatherTempC: number
  weatherCondition: string
  afrasiyobDuration: string
  remoteImageUrl?: string | null
  remoteAudioUrl?: string | null
  remoteAudioScript?: string | null
  isFromSupabase?: boolean
}

const TITLE_L10N: Record<string, Partial<Record<LangCode, string>>> = {
  registan: {
    uz: 'Registon Maydoni', ru: 'Площадь Регистан', tr: 'Registan Meydanı',
    zh: '雷吉斯坦广场', ja: 'レギスタン広場', de: 'Registan-Platz',
    fr: 'Place du Régistan', es: 'Plaza de Registán',
  },
  bukhara_kalyan: {
    uz: 'Poyi Kalon Majmuasi', ru: 'Комплекс Пои Калян', tr: 'Poi Kalyan Külliyesi',
    zh: '卡扬建筑群', ja: 'ポイ・カリャン複合体', de: 'Poi-Kalyan-Komplex',
    fr: "Complexe Po-i-Kalyan", es: 'Complejo Poi Kalyan',
  },
  khiva_ichan_kala: {
    uz: "Ichan Qal'a Qasri", ru: 'Крепость Ичан-Кала', tr: 'İçan Kale',
    zh: '伊钦卡拉古城', ja: 'イチャン・カラ城塞', de: 'Ichan-Kala-Festung',
    fr: "Citadelle d'Itchan Kala", es: 'Fortaleza de Ichan Kala',
  },
  chimgan_mountains: {
    uz: "Chimyon va Zomin Tog'lari", ru: 'Чимган и Зааминские Горы', tr: 'Çimgan ve Zaamin Dağları',
    zh: '齐姆甘与扎明山脉', ja: 'チムガン＆ザーミン山脈', de: 'Tschimgan- & Zaamin-Gipfel',
    fr: 'Pics de Tchimgan et Zaamin', es: 'Picos de Chimgan y Zaamin',
  },
}

const CITY_L10N: Record<string, Partial<Record<LangCode, string>>> = {
  samarkand: { uz: 'Samarqand', ru: 'Самарканд', tr: 'Semerkant', zh: '撒马尔罕', ja: 'サマルカンド' },
  bukhara: { uz: 'Buxoro', ru: 'Бухара', tr: 'Buhara', zh: '布哈拉', ja: 'ブハラ' },
  khiva: { uz: 'Xiva', ru: 'Хива', tr: 'Hive', zh: '希瓦', ja: 'ヒヴァ' },
  tashkent: { uz: 'Toshkent', ru: 'Ташкент', tr: 'Taşkent', zh: '塔什干', ja: 'タシュケント' },
}

const SUBTITLE_L10N: Record<string, Partial<Record<LangCode, string>>> = {
  registan: {
    uz: "Temuriylar Uyg'onish Davrining Markazi", ru: 'Сердце Тимуридского Ренессанса',
    tr: "Timur Rönesansı'nın Kalbi", zh: '帖木儿文艺复兴的中心', ja: 'ティムール朝ルネサンスの中心',
    de: 'Herz der Timuriden-Renaissance', fr: 'Cœur de la Renaissance timouride',
    es: 'Corazón del Renacimiento Timúrida',
  },
  bukhara_kalyan: {
    uz: 'Abadiyat Minorasi va Muqaddas Madrasa', ru: 'Башня Вечности и Священное Медресе',
    tr: 'Ebediyet Kulesi ve Kutsal Medrese', zh: '永恒之塔与神圣伊斯兰学院', ja: '永遠の塔と神聖なマドラサ',
  },
  khiva_ichan_kala: {
    uz: "Ipak Yo'lining Ochiq Osmon Ostidagi Tirik Muzeyi", ru: 'Живой музей под открытым небом Шелкового пути',
    tr: "İpek Yolu'nun Açık Hava Müzesi", zh: '丝绸之路露天鲜活博物馆', ja: 'シルクロードの野外生きた博物館',
  },
  chimgan_mountains: {
    uz: "O'zbekistonning Alp Tog'lari Go'zalligi", ru: 'Альпийский рай Узбекистана',
    tr: "Özbekistan'ın Alp Cenneti", zh: '乌兹别克斯坦的高山仙境', ja: 'ウズベキスタンの高山天国',
  },
}

export function localizedTitle(d: Destination, lang: LangCode): string {
  return TITLE_L10N[d.id]?.[lang] ?? d.title
}
export function localizedCity(d: Destination, lang: LangCode): string {
  const key = d.city.toLowerCase()
  const map = CITY_L10N[key] ?? (key === 'tashkent region' ? CITY_L10N.tashkent : undefined)
  return map?.[lang] ?? d.city
}
export function localizedSubtitle(d: Destination, lang: LangCode): string {
  return SUBTITLE_L10N[d.id]?.[lang] ?? d.subtitle
}

export const CATEGORY_KEYS: Record<DestinationCategory, string> = {
  ALL: 'cat_all',
  UNESCO: 'cat_unesco',
  SACRED: 'cat_sacred',
  SILK_ROAD: 'cat_fortress',
  NATURE: 'cat_nature',
  GASTRONOMY: 'cat_dining',
}

export const CROWD_KEYS: Record<CrowdLevel, string> = {
  LOW: 'crowd_low',
  MODERATE: 'crowd_moderate',
  HIGH: 'crowd_high',
}

export const CROWD_COLORS: Record<CrowdLevel, string> = {
  LOW: '#10B981',
  MODERATE: '#F59E0B',
  HIGH: '#EF4444',
}

export const SAMPLE_DESTINATIONS: Destination[] = [
  {
    id: 'registan',
    title: 'Registan Square',
    city: 'Samarkand',
    subtitle: 'Heart of the Timurid Renaissance',
    description:
      "Registan is the legendary crown jewel of Samarkand, framed by three monumental madrasahs (Ulugh Beg, Sher-Dor, and Tilya-Kori) dazzling with azure lapis lazuli mosaics and golden domes.",
    historyFact:
      'Commissioned by Ulugh Beg and later reconstructed in the 17th century, the acoustic courtyard was designed so a whisper in one archway echoes 40 meters across the tiled piazza.',
    unescoYear: 2001,
    rating: 4.98,
    reviewCount: 4820,
    category: 'UNESCO',
    image: '/images/img_registan_1787819168326.jpg',
    tag: 'Must Visit #1',
    coordinates: [39.6547, 66.9758],
    distanceKmFromTashkent: 310,
    bestTimeToVisit: 'Golden Hour (18:00 - 21:00)',
    highlights: [
      'Tilya-Kori Gilded Ceiling',
      'Sher-Dor Tiger Mosaics',
      'Ulugh Beg Astronomy Courtyard',
      'Light & Sound Holographic Show',
    ],
    architecturalPeriod: 'Timurid & Janid Eras (1417–1660)',
    crowdLevel: 'MODERATE',
    weatherTempC: 25,
    weatherCondition: 'Clear Azure Sky',
    afrasiyobDuration: '2h 13m',
  },
  {
    id: 'bukhara_kalyan',
    title: 'Poi Kalyan Complex',
    city: 'Bukhara',
    subtitle: 'The Tower of Eternity & Sacred Madrasah',
    description:
      "Rising 45 meters above Bukhara's desert skyline, the Kalyan Minaret survived Genghis Khan's conquest due to its breathtaking ornamental brickwork and harmonious Islamic proportions.",
    historyFact:
      'Built in 1127 AD by Karakhanid ruler Arslan Khan, its 14 distinct geometric brick bands use no blue tiles, relying purely on natural shadows cast by raw terracotta.',
    unescoYear: 1993,
    rating: 4.95,
    reviewCount: 3410,
    category: 'UNESCO',
    image: '/images/img_bukhara_1787819199064.jpg',
    tag: 'Historic Wonder',
    coordinates: [39.7758, 64.4158],
    distanceKmFromTashkent: 570,
    bestTimeToVisit: 'Sunrise (06:30 - 08:30)',
    highlights: [
      '45m Kalyan Minaret',
      'Mir-i-Arab 100-Dome Madrasah',
      'Ancient Brick Arabesque Arches',
      'Spiced Tea Caravan Teahouses',
    ],
    architecturalPeriod: 'Karakhanid & Shaybanid (1127–1536)',
    crowdLevel: 'LOW',
    weatherTempC: 27,
    weatherCondition: 'Golden Sunshine',
    afrasiyobDuration: '3h 45m',
  },
  {
    id: 'khiva_ichan_kala',
    title: 'Ichan Kala Fortress',
    city: 'Khiva',
    subtitle: 'Living Open-Air Museum of the Silk Road',
    description:
      'Step through the fortress gates into an untouched 18th-century clay citadel with turquoise-tiled minarets, marble pillars, and labyrinthine Silk Road alleyways.',
    historyFact:
      'The Kalta Minor minaret was originally designed to be tall enough to see all the way to Bukhara (400km away), but construction stopped when the Khan died in battle in 1855.',
    unescoYear: 1990,
    rating: 4.92,
    reviewCount: 2890,
    category: 'SILK_ROAD',
    image: '/images/img_khiva_1787819215477.jpg',
    tag: 'Living Legend',
    coordinates: [41.3783, 60.3639],
    distanceKmFromTashkent: 990,
    bestTimeToVisit: 'Sunset & Night Illumination',
    highlights: [
      'Kalta Minor Turquoise Minaret',
      'Juma Mosque 218 Carved Pillars',
      'Tosh Hovli Stone Palace',
      'Sunset View from Watchtower',
    ],
    architecturalPeriod: 'Khiva Khanate (17th–19th Century)',
    crowdLevel: 'MODERATE',
    weatherTempC: 28,
    weatherCondition: 'Warm Desert Breeze',
    afrasiyobDuration: 'Overnight Express (6h)',
  },
  {
    id: 'chimgan_mountains',
    title: 'Chimgan & Zaamin Peaks',
    city: 'Tashkent Region',
    subtitle: 'The Alpine Heaven of Uzbekistan',
    description:
      'Crisp mountain air, turquoise alpine lakes like Charvak, and dramatic snowy mountain ridges of the Western Tian Shan range offering paragliding, hiking, and pristine tranquility.',
    historyFact:
      'Part of the UNESCO Western Tian Shan biosphere, these mountain passes served as northern summer trade routes where caravans escaped the scorching desert heat.',
    unescoYear: 2016,
    rating: 4.88,
    reviewCount: 1950,
    category: 'NATURE',
    image: '/images/img_chimgan_mountains_1787819241162.jpg',
    tag: 'Alpine Adventure',
    coordinates: [41.52, 70.01],
    distanceKmFromTashkent: 85,
    bestTimeToVisit: 'Spring & Summer Morning',
    highlights: [
      'Charvak Turquoise Reservoir',
      'Amirsoy Scenic Cable Cars',
      'Gulkam Water Canyons',
      'Fresh Mountain Shashlik & Ayran',
    ],
    architecturalPeriod: 'Western Tian Shan Biosphere',
    crowdLevel: 'LOW',
    weatherTempC: 20,
    weatherCondition: 'Fresh Alpine Breeze',
    afrasiyobDuration: '1h 15m scenic drive',
  },
]

export type LangCode = 'uz' | 'en' | 'ru' | 'de' | 'fr' | 'es' | 'tr' | 'zh' | 'ja'

export interface AppLanguage {
  code: LangCode
  displayName: string
  flag: string
  nativeName: string
  greeting: string
}

export const LANGUAGES: AppLanguage[] = [
  { code: 'uz', displayName: "O'zbekcha", flag: '🇺🇿', nativeName: "O'zbek", greeting: 'Xush kelibsiz!' },
  { code: 'en', displayName: 'English', flag: '🇬🇧', nativeName: 'English', greeting: 'Welcome!' },
  { code: 'ru', displayName: 'Русский', flag: '🇷🇺', nativeName: 'Русский', greeting: 'Добро пожаловать!' },
  { code: 'de', displayName: 'Deutsch', flag: '🇩🇪', nativeName: 'Deutsch', greeting: 'Willkommen!' },
  { code: 'fr', displayName: 'Français', flag: '🇫🇷', nativeName: 'Français', greeting: 'Bienvenue!' },
  { code: 'es', displayName: 'Español', flag: '🇪🇸', nativeName: 'Español', greeting: '¡Bienvenido!' },
  { code: 'tr', displayName: 'Türkçe', flag: '🇹🇷', nativeName: 'Türkçe', greeting: 'Hoş geldiniz!' },
  { code: 'zh', displayName: '中文', flag: '🇨🇳', nativeName: '中文', greeting: '欢迎！' },
  { code: 'ja', displayName: '日本語', flag: '🇯🇵', nativeName: '日本語', greeting: 'ようこそ！' },
]

import { uz } from './uz'
import { en } from './en'
import { ru } from './ru'
import { de } from './de'
import { fr } from './fr'
import { es } from './es'
import { tr } from './tr'
import { zh } from './zh'
import { ja } from './ja'

const DICTS: Record<LangCode, Record<string, string>> = { uz, en, ru, de, fr, es, tr, zh, ja }

export function t(key: string, lang: LangCode): string {
  const dict = DICTS[lang]
  return dict[key] ?? en[key] ?? uz[key] ?? key
}

export function langByCode(code: string): AppLanguage {
  return LANGUAGES.find((l) => l.code === code) ?? LANGUAGES[0]
}

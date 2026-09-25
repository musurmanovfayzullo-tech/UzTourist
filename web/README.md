# UzTourist Web — PWA

O'zbekiston sayyohlik ilovasining web (PWA) versiyasi. Android ilovadan portlangan.

## Texnologiyalar

- **Vite + React 18 + TypeScript** — frontend
- **Tailwind CSS** — dizayn
- **vite-plugin-pwa (Workbox)** — offline PWA, service worker
- **Zustand** — holat boshqaruvi (persist bilan)
- **Supabase** — REST + Realtime (bookings, users, attractions, admin_logs)
- **Leaflet + OSM** — xarita
- **Web Speech API** — TTS/STT audio gid
- **Gemini Vision** — AR yodgorlik tanish (ixtiyoriy, `VITE_GEMINI_API_KEY`)
- **Telegram Bot API** — SOS va buyurtma bildirishnomalari

## Ishga tushirish

```powershell
cd web
npm install
npm run dev      # http://localhost:5173
npm run build    # production build -> dist/
npm run preview  # build'ni ko'rish
```

## Muhit o'zgaruvchilari

`.env` fayl yarating (ixtiyoriy):

```env
VITE_GEMINI_API_KEY=your_gemini_key
```

Supabase va Telegram sozlamalari `src/data/sos.ts` da.

## Ekranlar

- Splash → Registration (9 til) → Home
- AR Guide (kamera + Gemini/local tanish, TTS, "O'sha davr" overlay)
- Map (Leaflet, yodgorlik markerlari, GPS masofa)
- Tariffs (3 paket, to'lov, chek), Tickets (avia/poyezd, PNR)
- Staff Booking (gidlar), Offers (taksi/ovqat/mehmonxona)
- Expenses (depozit, konverter, byudjet, chayqa puli)
- Audio Tour (TTS transkriptlar), Showcase, SOS, Admin (PIN: `admin2026`)

## Papka tuzilishi

```
web/src/
  data/       — portlangan modellar (destinations, tariffs, staff, tickets, offers, arFacts, currency, audio, sos)
  i18n/       — 9 til lug'atlari + t() funksiyasi
  services/   — supabase, telegram, geo, speech, gemini
  store/      — appStore.ts (Zustand)
  screens/    — 14 ekran
  components/ — BottomNav
```

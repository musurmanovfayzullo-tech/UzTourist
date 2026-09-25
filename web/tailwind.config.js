/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        registan: { DEFAULT: '#0047AB', dark: '#002D6B', light: '#1E6AFF' },
        turquoise: { DEFAULT: '#00A896', glaze: '#00C49F' },
        lapis: '#0A2463',
        silk: { DEFAULT: '#D4AF37', light: '#FFDF73', dark: '#997D1E' },
        neon: '#F6C845',
        sand: '#F3E5AB',
        midnight: { DEFAULT: '#050B1A', surface: '#0C162E', variant: '#132247' },
        ruby: '#D81E5B',
        emerald: '#10B981',
      },
      fontFamily: {
        display: ['"Playfair Display"', 'Georgia', 'serif'],
        body: ['Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        gold: '0 8px 30px -8px rgba(246, 200, 69, 0.45)',
        glass: '0 10px 40px -12px rgba(0, 0, 0, 0.35)',
      },
      keyframes: {
        scan: { '0%': { top: '0%' }, '100%': { top: '100%' } },
        pulseRing: { '0%': { transform: 'scale(0.9)', opacity: '0.8' }, '100%': { transform: 'scale(1.6)', opacity: '0' } },
        shimmer: { '0%': { backgroundPosition: '-200% 0' }, '100%': { backgroundPosition: '200% 0' } },
        float: { '0%,100%': { transform: 'translateY(0)' }, '50%': { transform: 'translateY(-6px)' } },
        spinSlow: { to: { transform: 'rotate(360deg)' } },
      },
      animation: {
        scan: 'scan 2.2s linear infinite',
        pulseRing: 'pulseRing 1.6s ease-out infinite',
        shimmer: 'shimmer 3s linear infinite',
        float: 'float 4s ease-in-out infinite',
        spinSlow: 'spinSlow 24s linear infinite',
      },
    },
  },
  plugins: [],
}

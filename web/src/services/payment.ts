// Card validation + formatting helpers for the checkout flow.
// NOTE: this validates input only — it does NOT charge the card. Real charging
// requires a payment gateway (Payme/Click) with a merchant backend.

/** Luhn checksum — catches mistyped / invalid card numbers. */
export function luhnValid(num: string): boolean {
  const digits = num.replace(/\D/g, '')
  if (digits.length < 13 || digits.length > 19) return false
  let sum = 0
  let dbl = false
  for (let i = digits.length - 1; i >= 0; i--) {
    let d = +digits[i]
    if (dbl) {
      d *= 2
      if (d > 9) d -= 9
    }
    sum += d
    dbl = !dbl
  }
  return sum % 10 === 0
}

/** Detect card network from the number prefix. */
export function cardType(num: string): string {
  const d = num.replace(/\D/g, '')
  if (/^8600/.test(d)) return 'Uzcard'
  if (/^9860/.test(d)) return 'Humo'
  if (/^4/.test(d)) return 'Visa'
  if (/^5[1-5]/.test(d)) return 'Mastercard'
  if (/^2/.test(d)) return 'Mir'
  if (/^3[47]/.test(d)) return 'Amex'
  if (/^62/.test(d)) return 'UnionPay'
  return 'Karta'
}

/** Format a card number as groups of 4: "8600 1234 5678 9012". */
export function formatCardNumber(num: string): string {
  return num.replace(/\D/g, '').slice(0, 19).replace(/(\d{4})(?=\d)/g, '$1 ')
}

/** Format expiry as MM/YY while typing. */
export function formatExpiry(v: string): string {
  const d = v.replace(/\D/g, '').slice(0, 4)
  if (d.length <= 2) return d
  return `${d.slice(0, 2)}/${d.slice(2)}`
}

/** Expiry must be MM/YY, valid month, and not in the past. */
export function expiryValid(exp: string): boolean {
  const m = exp.match(/^(\d{2})\/(\d{2})$/)
  if (!m) return false
  const mm = +m[1]
  const yy = +m[2]
  if (mm < 1 || mm > 12) return false
  const now = new Date()
  const curYY = now.getFullYear() % 100
  const curMM = now.getMonth() + 1
  if (yy < curYY || (yy === curYY && mm < curMM)) return false
  return true
}

/** CVV is 3-4 digits. */
export function cvvValid(cvv: string): boolean {
  return /^\d{3,4}$/.test(cvv)
}

/** Full card validation — returns an error key or null if valid. */
export function validateCard(num: string, exp: string, cvv: string): string | null {
  const digits = num.replace(/\D/g, '')
  if (digits.length < 16) return 'pay_err_card_short'
  if (!luhnValid(digits)) return 'pay_err_card_invalid'
  if (!expiryValid(exp)) return 'pay_err_expiry'
  if (!cvvValid(cvv)) return 'pay_err_cvv'
  return null
}

import { useEffect, useMemo, useState } from 'react'
import { Plus, Wallet, TrendingUp, Info, X, CreditCard } from 'lucide-react'
import { useAppStore, useT } from '../store/appStore'
import { CURRENCIES, EXPENSE_CATEGORIES, TIPPING_GUIDES } from '../data/currency'
import { formatUzs } from '../data/tariffs'
import { validateCard, formatCardNumber, formatExpiry, cardType } from '../services/payment'
import { fetchUzsRates } from '../services/fx'

export default function ExpensesScreen() {
  const t = useT()
  const expenses = useAppStore((s) => s.expenses)
  const depositUzs = useAppStore((s) => s.depositUzs)
  const topUpDeposit = useAppStore((s) => s.topUpDeposit)
  const addExpense = useAppStore((s) => s.addExpense)

  const [fromCur, setFromCur] = useState('USD')
  const [amount, setAmount] = useState('100')
  const [showAdd, setShowAdd] = useState(false)
  const [newTitle, setNewTitle] = useState('')
  const [newAmount, setNewAmount] = useState('')
  const [newCat, setNewCat] = useState(EXPENSE_CATEGORIES[0].id)
  const [showTips, setShowTips] = useState(false)
  const [topUpAmt, setTopUpAmt] = useState(0)
  const [cardNum, setCardNum] = useState('')
  const [cardExp, setCardExp] = useState('')
  const [cardCvv, setCardCvv] = useState('')
  const [topUpErr, setTopUpErr] = useState('')
  const [topUpDone, setTopUpDone] = useState(false)

  const [liveRates, setLiveRates] = useState<Record<string, number> | null>(null)
  useEffect(() => {
    let alive = true
    fetchUzsRates().then((r) => { if (alive) setLiveRates(r) })
    return () => { alive = false }
  }, [])

  const rate = liveRates?.[fromCur] ?? CURRENCIES.find((c) => c.code === fromCur)?.rateToUzs ?? 1
  const converted = (parseFloat(amount) || 0) * rate

  const totalSpent = useMemo(() => expenses.reduce((s, e) => s + e.amountUzs, 0), [expenses])
  const totalAllocated = EXPENSE_CATEGORIES.reduce((s, c) => s + c.allocatedUzs, 0)

  const catSpent = (id: string) => expenses.filter((e) => e.categoryId === id).reduce((s, e) => s + e.amountUzs, 0)

  const submitExpense = () => {
    const amt = parseInt(newAmount.replace(/\D/g, ''), 10)
    if (!newTitle.trim() || !amt) return
    addExpense({
      id: `exp_${Date.now()}`,
      title: newTitle.trim(),
      categoryId: newCat,
      amountUzs: amt,
      timeAgo: new Date().toLocaleString('uz-UZ'),
      city: 'Samarqand',
    })
    setNewTitle('')
    setNewAmount('')
    setShowAdd(false)
  }

  const openTopUp = (v: number) => {
    setTopUpAmt(v)
    setTopUpErr('')
    setTopUpDone(false)
    setCardNum('')
    setCardExp('')
    setCardCvv('')
  }

  const confirmTopUp = () => {
    const err = validateCard(cardNum, cardExp, cardCvv)
    if (err) {
      setTopUpErr(t(err))
      return
    }
    topUpDeposit(topUpAmt)
    setTopUpDone(true)
  }

  return (
    <div className="mx-auto min-h-screen max-w-lg px-4 pb-28 pt-5">
      <h1 className="text-lg font-bold text-amber-400">{t('exp_title')}</h1>

      {/* Deposit card */}
      <div className="mt-4 rounded-3xl bg-gradient-to-br from-amber-500 to-orange-700 p-5 text-slate-950 shadow-xl">
        <div className="flex items-center justify-between">
          <p className="text-xs font-semibold uppercase tracking-wide opacity-80">{t('exp_deposit')}</p>
          <Wallet size={20} />
        </div>
        <p className="mt-1 text-3xl font-black">{formatUzs(depositUzs)}</p>
        <div className="mt-3 flex gap-2">
          {[500000, 1000000, 2000000].map((v) => (
            <button
              key={v}
              onClick={() => openTopUp(v)}
              className="rounded-full bg-slate-950/20 px-3 py-1.5 text-xs font-bold"
            >
              +{(v / 1000).toFixed(0)}K
            </button>
          ))}
        </div>
      </div>

      {/* Converter */}
      <div className="mt-5 rounded-3xl border border-white/10 bg-white/5 p-4">
        <h2 className="text-sm font-semibold text-slate-200">{t('exp_converter')}</h2>
        <div className="mt-3 flex gap-2">
          <select
            value={fromCur}
            onChange={(e) => setFromCur(e.target.value)}
            className="rounded-xl border border-white/10 bg-slate-900 px-3 py-2.5 text-sm"
          >
            {CURRENCIES.map((c) => (
              <option key={c.code} value={c.code}>{c.flag} {c.code}</option>
            ))}
          </select>
          <input
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            inputMode="decimal"
            className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm outline-none focus:border-amber-400"
          />
        </div>
        <p className="mt-2 text-right text-lg font-bold text-emerald-400">= {formatUzs(converted)}</p>
      </div>

      {/* Budget bars */}
      <div className="mt-5 rounded-3xl border border-white/10 bg-white/5 p-4">
        <div className="flex items-center justify-between">
          <h2 className="text-sm font-semibold text-slate-200">{t('exp_budget')}</h2>
          <span className="flex items-center gap-1 text-xs text-slate-400">
            <TrendingUp size={13} /> {formatUzs(totalSpent)} / {formatUzs(totalAllocated)}
          </span>
        </div>
        <div className="mt-3 space-y-3">
          {EXPENSE_CATEGORIES.map((c) => {
            const spent = catSpent(c.id) || c.spentUzs
            const pct = Math.min(100, (spent / c.allocatedUzs) * 100)
            return (
              <div key={c.id}>
                <div className="flex justify-between text-xs">
                  <span>{c.icon} {c.title}</span>
                  <span className="text-slate-400">{formatUzs(spent)}</span>
                </div>
                <div className="mt-1 h-2 overflow-hidden rounded-full bg-white/10">
                  <div
                    className={`h-full rounded-full ${pct > 85 ? 'bg-red-500' : pct > 60 ? 'bg-amber-500' : 'bg-emerald-500'}`}
                    style={{ width: `${pct}%` }}
                  />
                </div>
              </div>
            )
          })}
        </div>
      </div>

      {/* Expense list */}
      <div className="mt-5 flex items-center justify-between">
        <h2 className="text-sm font-semibold text-slate-200">{t('exp_history')}</h2>
        <button onClick={() => setShowAdd(!showAdd)} className="flex items-center gap-1 rounded-xl bg-amber-500/15 px-3 py-1.5 text-xs font-semibold text-amber-300">
          <Plus size={14} /> {t('exp_add')}
        </button>
      </div>

      {showAdd && (
        <div className="mt-3 space-y-2 rounded-2xl border border-white/10 bg-white/5 p-4">
          <input value={newTitle} onChange={(e) => setNewTitle(e.target.value)} placeholder={t('exp_name')} className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm outline-none focus:border-amber-400" />
          <input value={newAmount} onChange={(e) => setNewAmount(e.target.value)} placeholder={t('exp_amount')} inputMode="numeric" className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm outline-none focus:border-amber-400" />
          <select value={newCat} onChange={(e) => setNewCat(e.target.value)} className="w-full rounded-xl border border-white/10 bg-slate-900 px-3 py-2.5 text-sm">
            {EXPENSE_CATEGORIES.map((c) => <option key={c.id} value={c.id}>{c.icon} {c.title}</option>)}
          </select>
          <button onClick={submitExpense} className="w-full rounded-xl bg-amber-500 py-2.5 text-sm font-bold text-slate-950">{t('save')}</button>
        </div>
      )}

      <div className="mt-3 space-y-2">
        {expenses.map((e) => {
          const cat = EXPENSE_CATEGORIES.find((c) => c.id === e.categoryId)
          return (
            <div key={e.id} className="flex items-center gap-3 rounded-2xl border border-white/10 bg-white/5 p-3.5">
              <span className="text-xl">{cat?.icon ?? '💸'}</span>
              <div className="min-w-0 flex-1">
                <p className="truncate text-xs font-semibold">{e.title}</p>
                <p className="text-[10px] text-slate-400">{e.city} • {e.timeAgo}</p>
              </div>
              <p className="text-xs font-bold text-red-400">-{formatUzs(e.amountUzs)}</p>
            </div>
          )
        })}
      </div>

      {/* Tipping guide */}
      <button onClick={() => setShowTips(!showTips)} className="mt-5 flex w-full items-center gap-2 rounded-2xl border border-white/10 bg-white/5 p-4 text-sm font-semibold text-slate-200">
        <Info size={16} className="text-sky-400" /> {t('exp_tipping')}
      </button>
      {showTips && (
        <div className="mt-2 space-y-2">
          {TIPPING_GUIDES.map(([title, desc]) => (
            <div key={title} className="rounded-2xl border border-white/10 bg-white/5 p-4">
              <p className="text-xs font-bold text-amber-300">{title}</p>
              <p className="mt-1 text-xs leading-relaxed text-slate-300">{desc}</p>
            </div>
          ))}
        </div>
      )}

      {/* Top-up sheet — requires valid card details */}
      {topUpAmt > 0 && (
        <div className="fixed inset-0 z-[70] flex items-end justify-center bg-black/70 backdrop-blur-sm" onClick={() => setTopUpAmt(0)}>
          <div className="w-full max-w-lg rounded-t-3xl border-t border-white/10 bg-slate-950 p-5 pb-[calc(1.25rem+env(safe-area-inset-bottom))]" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-bold text-slate-100">{t('exp_deposit')} • {formatUzs(topUpAmt)}</h3>
              <button onClick={() => setTopUpAmt(0)} className="rounded-full bg-white/10 p-1.5"><X size={16} /></button>
            </div>

            {topUpDone ? (
              <div className="mt-4 rounded-2xl border border-emerald-500/30 bg-emerald-500/10 p-4 text-center">
                <p className="text-sm font-bold text-emerald-300">{t('pay_success')}</p>
                <p className="mt-1 text-xs text-slate-300">+{formatUzs(topUpAmt)}</p>
                <button onClick={() => setTopUpAmt(0)} className="mt-3 w-full rounded-xl bg-emerald-500 py-2.5 text-sm font-bold text-slate-950">{t('close')}</button>
              </div>
            ) : (
              <div className="mt-4 space-y-2">
                <div className="relative">
                  <input
                    value={cardNum}
                    onChange={(e) => setCardNum(formatCardNumber(e.target.value))}
                    placeholder="8600 1234 5678 9012"
                    inputMode="numeric"
                    className="w-full rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 pr-20 text-sm tracking-widest outline-none focus:border-amber-400"
                  />
                  {cardNum.replace(/\D/g, '').length >= 4 && (
                    <span className="absolute right-3 top-1/2 -translate-y-1/2 rounded bg-white/10 px-1.5 py-0.5 text-[9px] font-bold text-sky-300">{cardType(cardNum)}</span>
                  )}
                </div>
                <div className="grid grid-cols-2 gap-2">
                  <input
                    value={cardExp}
                    onChange={(e) => setCardExp(formatExpiry(e.target.value))}
                    placeholder="MM/YY"
                    inputMode="numeric"
                    className="rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm tracking-widest outline-none focus:border-amber-400"
                  />
                  <input
                    value={cardCvv}
                    onChange={(e) => setCardCvv(e.target.value.replace(/\D/g, '').slice(0, 4))}
                    placeholder="CVV"
                    inputMode="numeric"
                    type="password"
                    className="rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-sm tracking-widest outline-none focus:border-amber-400"
                  />
                </div>
                {topUpErr && <p className="text-xs font-semibold text-red-400">{topUpErr}</p>}
                <button onClick={confirmTopUp} className="flex w-full items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-amber-500 to-orange-600 py-3 text-sm font-bold text-slate-950">
                  <CreditCard size={16} /> {t('pay_confirm')} • {formatUzs(topUpAmt)}
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  )
}

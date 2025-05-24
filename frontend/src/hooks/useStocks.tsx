// src/hooks/useStocks.tsx
import { useState, useEffect } from 'react'
import axios from 'axios'
import type { StockData } from '../models/stockData'
import type { StockSymbol } from '../models/stockSymbol'
import type { StockCandle } from '../models/stockCandle'

const API_BASE = 'http://localhost:8080/stocks'

function getAuthHeaders() {
    const token = sessionStorage.getItem('token') || ''
    return { headers: { Authorization: `Bearer ${token}` } }
}

export function useMonthlyData(symbol: StockSymbol) {
    const [data, setData] = useState<StockData[] | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<Error | null>(null)

    useEffect(() => {
        if (!symbol) return
        setLoading(true)
        setError(null)

        axios
            .get<{ symbol: string; prices: Record<string, StockCandle> }>(
                `${API_BASE}/${symbol}/monthly`,
                getAuthHeaders()
            )
            .then(res => {
                const raw = res.data.prices
                const arr: StockData[] = Object.entries(raw).map(([date, candle]) => ({
                    date,
                    candle,
                }))
                setData(arr)
            })
            .catch(err => setError(err))
            .finally(() => setLoading(false))
    }, [symbol])

    return { data, loading, error }
}

export function useDailyData(symbol: StockSymbol, date: string | null) {
    const [data, setData] = useState<StockData[] | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<Error | null>(null)

    useEffect(() => {
        if (!symbol) return
        setLoading(true)
        setError(null)

        const query = date ? `?date=${date}` : ''
        axios
            .get<{ symbol: string; prices: Record<string, StockCandle> }>(
                `${API_BASE}/${symbol}/daily${query}`,
                getAuthHeaders()
            )
            .then(res => {
                const raw = res.data.prices
                const arr: StockData[] = Object.entries(raw).map(([dt, candle]) => ({
                    date: dt,
                    candle,
                }))
                setData(arr)
            })
            .catch(err => setError(err))
            .finally(() => setLoading(false))
    }, [symbol, date])

    return { data, loading, error }
}

export function useCurrentPrice(symbol: StockSymbol) {
    const [price, setPrice] = useState<number | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<Error | null>(null)

    useEffect(() => {
        if (!symbol) return
        setLoading(true)
        setError(null)

        axios
            .get<number>(`${API_BASE}/${symbol}/current`, getAuthHeaders())
            .then(res => setPrice(res.data))
            .catch(err => setError(err))
            .finally(() => setLoading(false))
    }, [symbol])

    return { price, loading, error }
}

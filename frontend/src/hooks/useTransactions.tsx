import { useState, useEffect, useCallback } from 'react'
import axios from 'axios'
import type Transaction from '../models/transaction'
import type { CreateTransactionDTO } from '../models/createTransactionDTO'
import { API_BASE_URL } from '../constants/api'

function getAuthHeaders() {
    const token = sessionStorage.getItem('token') || ''
    return { headers: { Authorization: `Bearer ${token}` } }
}

export function useTransactions() {
    const [transactions, setTransactions] = useState<Transaction[]>([])
    const [loading, setLoading]         = useState(false)
    const [error, setError]             = useState<Error | null>(null)

    const fetchTransactions = useCallback(() => {
        setLoading(true)
        setError(null)
        axios
            .get<Transaction[]>(`${API_BASE_URL}/transaction/all`, getAuthHeaders())
            .then(res => setTransactions(res.data))
            .catch(err => setError(err))
            .finally(() => setLoading(false))
    }, [])

    // BUY
    const addBuy = useCallback((
        portfolioId: string,
        dto: CreateTransactionDTO
    ) => {
        setLoading(true)
        setError(null)
        return axios
            .post<Transaction>(
                `${API_BASE_URL}/transaction/buy/${portfolioId}`,
                dto,
                getAuthHeaders()
            )
            .then(res => {
                fetchTransactions()
                return res.data
            })
            .catch(err => {
                setError(err)
                throw err
            })
            .finally(() => setLoading(false))
    }, [fetchTransactions])

    // SELL
    const addSell = useCallback((
        portfolioId: string,
        dto: CreateTransactionDTO
    ) => {
        setLoading(true)
        setError(null)
        return axios
            .post<Transaction>(
                `${API_BASE_URL}/transaction/sell/${portfolioId}`,
                dto,
                getAuthHeaders()
            )
            .then(res => {
                fetchTransactions()
                return res.data
            })
            .catch(err => {
                setError(err)
                throw err
            })
            .finally(() => setLoading(false))
    }, [fetchTransactions])

    useEffect(() => {
        fetchTransactions()
    }, [fetchTransactions])

    return {
        transactions,
        loading,
        error,
        fetchTransactions,
        addBuy,
        addSell,
    }
}

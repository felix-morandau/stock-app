// src/hooks/useSettings.ts
import { useState, useEffect, useCallback } from 'react'
import axios from 'axios'
import type { Language } from '../models/languages'
import type { Currency } from '../models/currencies.ts'
import { API_BASE_URL } from '../constants/api'
import type {Theme} from "../models/themes.ts";

interface UserSettings {
    language: Language
    currency: Currency
    theme: Theme
}

function getAuthHeaders() {
    const token = sessionStorage.getItem('token') || ''
    return { headers: { Authorization: `Bearer ${token}` } }
}

export function useSettings() {
    const [settings, setSettings]     = useState<UserSettings | null>(null)
    const [loading, setLoading]       = useState<boolean>(false)
    const [error, setError]           = useState<Error | null>(null)

    const currentUser = sessionStorage.getItem('userId') as string;

    const fetchSettings = useCallback(async () => {
        setLoading(true)
        setError(null)
        try {
            const res = await axios.get<UserSettings>(
                `${API_BASE_URL}/settings/${currentUser}`,
                getAuthHeaders()
            )
            setSettings(res.data)
        } catch (err) {
            setError(err as Error)
        } finally {
            setLoading(false)
        }
    }, [])

    const updateSettings = useCallback(
        async (partial: Partial<UserSettings>) => {
            if (!settings) return
            setLoading(true)
            setError(null)
            try {
                const res = await axios.put<UserSettings>(
                    `${API_BASE_URL}/settings/${currentUser}`,
                    partial,
                    getAuthHeaders()
                )
                setSettings(res.data)
            } catch (err) {
                setError(err as Error)
            } finally {
                setLoading(false)
            }
        },
        [settings]
    )

    useEffect(() => {
        fetchSettings()
    }, [fetchSettings])

    return {
        settings,
        loading,
        error,
        fetchSettings,
        updateSettings,
    }
}

// src/pages/SettingsPage.tsx
import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { LANGUAGES, type Language } from '../../models/languages'
import { CURRENCIES, type Currency } from '../../models/currencies'
import { THEMES, type Theme } from '../../models/themes'
import { useSettings } from '../../hooks/useSettings'
import Dropdown from '../../components/Dropdown'
import './SettingsPage.css'

export default function SettingsPage() {
    const navigate = useNavigate()
    const { settings, loading, error, updateSettings } = useSettings()

    const [language, setLanguage] = useState<Language>(LANGUAGES[0])
    const [currency, setCurrency] = useState<Currency>(CURRENCIES[0])
    const [theme, setTheme]       = useState<Theme>(THEMES[0])

    useEffect(() => {
        if (settings) {
            setLanguage(settings.language)
            setCurrency(settings.currency)
            setTheme(settings.theme)
        }
    }, [settings])

    const handleSave = () => {
        updateSettings({ language, currency, theme })
    }

    if (loading) return <p>Loading...</p>
    if (error)   return <p className="error-text">Error loading settings</p>

    return (
        <div className="settings-container">
            <button className="back-btn" onClick={() => navigate(-1)}>
                &larr; Back
            </button>
            <h2>Settings</h2>
            <div className="settings-form">
                <div className="form-row">
                    <span className="form-label">Language</span>
                    <Dropdown
                        options={LANGUAGES}
                        initialValue={language}
                        onChange={v => setLanguage(v as Language)}
                    />
                </div>
                <div className="form-row">
                    <span className="form-label">Currency</span>
                    <Dropdown
                        options={CURRENCIES}
                        initialValue={currency}
                        onChange={v => setCurrency(v as Currency)}
                    />
                </div>
                <div className="form-row">
                    <span className="form-label">Theme</span>
                    <Dropdown
                        options={THEMES}
                        initialValue={theme}
                        onChange={v => setTheme(v as Theme)}
                    />
                </div>
                <div className="form-actions">
                    <button className="save-btn" onClick={handleSave}>
                        Save
                    </button>
                </div>
            </div>
        </div>
    )
}

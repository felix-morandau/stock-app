// src/pages/ClientDashboardPage.tsx
import { useState } from 'react'
import { STOCK_SYMBOLS } from '../../models/stockSymbol'
import { APP_NAME } from '../../constants/merch'
import {
    useMonthlyData,
    useDailyData,
    useCurrentPrice
} from '../../hooks/useStocks'
import Graph from '../../components/Graph'
import './ClientDashboardPage.css'
import {useNavigate} from "react-router-dom";

export default function ClientDashboardPage() {
    const [selected, setSelected] = useState(STOCK_SYMBOLS[0])
    const [period, setPeriod]     = useState<'monthly'|'daily'>('monthly')
    const [date, setDate]         = useState('')

    const navigate = useNavigate();

    const { data: monthly, loading: loadM, error: errM } =
        useMonthlyData(selected)
    const { data: daily, loading: loadD, error: errD } =
        useDailyData(selected, period === 'daily' ? date : null)
    const { price, loading: loadP, error: errP } =
        useCurrentPrice(selected)

    return (
        <div className="page-container">
            <nav className="navbar">
                <div className="logo">
                    <span>📈 {APP_NAME}</span>
                </div>

                <div className="search-bar">
                    <input type="text" placeholder="Search stocks..." />
                </div>

                <div className="nav-actions">
                    <button className="nav-btn">Profile</button>
                    <button
                        className="nav-btn"
                        onClick={() => navigate('/client-settings')}
                    >Settings</button>
                </div>
            </nav>

            <div className="dashboard-container">
                <aside className="sidebar">
                    <ul>
                        {STOCK_SYMBOLS.map(sym => (
                            <li
                                key={sym}
                                className={sym === selected ? 'active' : ''}
                                onClick={() => setSelected(sym)}
                            >
                                {sym}
                            </li>
                        ))}
                    </ul>
                </aside>

                <main className="main-content">
                    <header className="header">
                        <h2>{selected}</h2>
                        <div className="price">
                            {loadP
                                ? 'Loading price...'
                                : price != null
                                    ? `${price} ${sessionStorage.getItem('currency')}`
                                    : 'Error'}
                        </div>
                    </header>

                    <section className="controls">
                        <label>
                            <input
                                type="radio"
                                name="period"
                                value="monthly"
                                checked={period === 'monthly'}
                                onChange={() => setPeriod('monthly')}
                            />
                            Last Month
                        </label>
                        <label>
                            <input
                                type="radio"
                                name="period"
                                value="daily"
                                checked={period === 'daily'}
                                onChange={() => setPeriod('daily')}
                            />
                            Daily
                        </label>
                        {period === 'daily' && (
                            <input
                                type="date"
                                value={date}
                                onChange={e => setDate(e.target.value)}
                            />
                        )}
                    </section>

                    <section className="graph-area">
                        {period === 'monthly' ? (
                            errM ? (
                                <p>Error loading monthly data</p>
                            ) : (
                                <Graph title="Monthly Prices" data={monthly || []} />
                            )
                        ) : errD ? (
                            <p>Error loading daily data</p>
                        ) : (
                            <Graph
                                title={`Daily Prices for ${date || 'Today'}`}
                                data={daily || []}
                            />
                        )}

                        <div className="button-container">
                            <button className="dashboard-button buy">BUY</button>
                            <button className="dashboard-button sell">SELL</button>
                        </div>
                    </section>
                </main>
            </div>
        </div>
    )
}

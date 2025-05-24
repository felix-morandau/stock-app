import { useState } from 'react';
import { STOCK_SYMBOLS } from '../../models/stockSymbol.ts';
import { useMonthlyData, useDailyData, useCurrentPrice } from '../../hooks/useStocks.tsx';
import Graph from '../../components/Graph.tsx';
import './ClientDashboardPage.css';

export default function ClientDashboardPage() {
    const [selected, setSelected] = useState(STOCK_SYMBOLS[0]);
    const [period, setPeriod] = useState<'monthly' | 'daily'>('monthly');
    const [date, setDate] = useState('');

    const { data: monthly, loading: loadM, error: errM } = useMonthlyData(selected);
    const { data: daily, loading: loadD, error: errD } = useDailyData(selected, period === 'daily' ? date : null);
    const { price, loading: loadP, error: errP } = useCurrentPrice(selected);

    return (
        <div className="dashboard-container">
            <aside className="sidebar">
                <ul>
                    {STOCK_SYMBOLS.map(sym => (
                        <li key={sym} className={sym === selected ? 'active' : ''} onClick={() => setSelected(sym)}>
                            {sym}
                        </li>
                    ))}
                </ul>
            </aside>

            <main className="main-content">
                <header className="header">
                    <h2>{selected} Dashboard</h2>
                    <div className="price">
                        {loadP ? 'Loading price...' : price != null ? `$${price}` : 'Error'}
                    </div>
                </header>

                <section className="controls">
                    <label>
                        <input type="radio" name="period" value="monthly" checked={period === 'monthly'} onChange={() => setPeriod('monthly')} /> Last Month
                    </label>
                    <label>
                        <input type="radio" name="period" value="daily" checked={period === 'daily'} onChange={() => setPeriod('daily')} /> Daily
                    </label>
                    {period === 'daily' && (
                        <input type="date" value={date} onChange={e => setDate(e.target.value)} />
                    )}
                </section>

                <section className="graph-area">
                    {period === 'monthly' ? (
                        errM ? <p>Error loading monthly data</p> : <Graph title="Monthly Prices" data={monthly} />
                    ) : (
                        errD ? <p>Error loading daily data</p> : <Graph title={`Daily Prices for ${date || 'Today'}`} data={daily} />
                    )}
                </section>
            </main>
        </div>
    );
}
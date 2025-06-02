import { useState, useCallback, useEffect } from 'react';
import { STOCK_SYMBOLS } from '../../models/stockSymbol';
import { APP_NAME } from '../../constants/merch';
import {
    useMonthlyData,
    useDailyData,
    useCurrentPrice
} from '../../hooks/useStocks';
import { useTransactions } from '../../hooks/useTransactions';
import { usePortfolios } from '../../hooks/usePortfolios';
import { useUsers } from '../../hooks/useUsers'; // Import the updated hook
import Graph from '../../components/Graph';
import Modal from '../../components/Modal';
import './ClientDashboardPage.css';
import { useNavigate } from "react-router-dom";
import type { CreateTransactionDTO } from "../../models/createTransactionDTO";
import {API_BASE_URL} from "../../constants/api.ts";

export default function ClientDashboardPage() {
    const [selected, setSelected] = useState(STOCK_SYMBOLS[0]);
    const [period, setPeriod] = useState<'monthly' | 'daily'>('monthly');
    const [date, setDate] = useState('');
    const [showModal, setShowModal] = useState<'buy' | 'sell' | null>(null);
    const [shares, setShares] = useState<number | ''>('');
    const [notes, setNotes] = useState('');

    const navigate = useNavigate();

    const { data: monthly, loading: loadM, error: errM } = useMonthlyData(selected);
    const { data: daily, loading: loadD, error: errD } = useDailyData(selected, period === 'daily' ? date : null);
    const { price, loading: loadP, error: errP } = useCurrentPrice(selected);
    const { addBuy, addSell, loading: txLoading, error: txError } = useTransactions();
    const { portfolios, loading: portLoading, error: portError } = usePortfolios();
    const { user, userLoading, userError, fetchUserById } = useUsers(); // Use the updated hook

    // Manage selected portfolio
    const [selectedPortfolioId, setSelectedPortfolioId] = useState<string | null>(
        sessionStorage.getItem('portfolioId') || null
    );

    // Fetch the current user's data on component mount
    useEffect(() => {
        const userId = sessionStorage.getItem('userId');
        if (userId) {
            fetchUserById(userId);
        }
    }, [fetchUserById]);

    useEffect(() => {
        if (portfolios && portfolios.length > 0 && !selectedPortfolioId) {
            const firstPortfolioId = portfolios[0].id;
            setSelectedPortfolioId(firstPortfolioId);
            sessionStorage.setItem('portfolioId', firstPortfolioId);
        }
    }, [portfolios, selectedPortfolioId]);

    const handlePortfolioChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newPortfolioId = event.target.value;
        setSelectedPortfolioId(newPortfolioId);
        sessionStorage.setItem('portfolioId', newPortfolioId);
    };

    const openModal = (type: 'buy' | 'sell') => {
        setShowModal(type);
        setShares('');
        setNotes('');
    };

    const closeModal = () => {
        setShowModal(null);
        setShares('');
        setNotes('');
    };

    const handleTransaction = useCallback(async (type: 'buy' | 'sell') => {
        if (shares === '' || shares <= 0) {
            alert('Please enter a valid number of shares.');
            return;
        }

        if (!price) {
            alert('Current price is not available. Please try again later.');
            return;
        }

        if (!selectedPortfolioId) {
            alert('Please select a portfolio.');
            return;
        }

        const dto: CreateTransactionDTO = {
            transactionType: type === 'buy' ? 'BUY' : 'SELL',
            notes: notes || '',
            stockName: selected,
            sharePrice: price,
            nrOfShares: shares,
        };

        try {
            if (type === 'buy') {
                await addBuy(selectedPortfolioId, dto);
                alert(`Successfully bought ${shares} shares of ${selected}!`);
                // Refetch user balance after a successful buy transaction
                const userId = sessionStorage.getItem('userId');
                if (userId) fetchUserById(userId);
            } else {
                await addSell(selectedPortfolioId, dto);
                alert(`Successfully sold ${shares} shares of ${selected}!`);
                // Refetch user balance after a successful sell transaction
                const userId = sessionStorage.getItem('userId');
                if (userId) fetchUserById(userId);
            }
            closeModal();
        } catch (err) {
            console.error('Transaction failed:', err);
            alert('Transaction failed: ' + (txError?.message || 'Unknown error'));
        }
    }, [selected, shares, price, notes, selectedPortfolioId, addBuy, addSell, txLoading, txError, fetchUserById]);

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
                    <select
                        value={selectedPortfolioId || ''}
                        onChange={handlePortfolioChange}
                        disabled={portLoading || !portfolios || portfolios.length === 0}
                        className="portfolio-dropdown"
                    >
                        <option value="" disabled>Select Portfolio</option>
                        {portfolios?.map(portfolio => (
                            <option key={portfolio.id} value={portfolio.id}>
                                {portfolio.name}
                            </option>
                        ))}
                    </select>
                    {portLoading && <span>Loading portfolios...</span>}
                    {portError && <span>Error loading portfolios</span>}
                    <span className="user-balance">
                        {userLoading ? (
                            'Loading balance...'
                        ) : userError ? (
                            'Error loading balance'
                        ) : user ? (
                            `Balance: ${user.balance.toFixed(2)} ${sessionStorage.getItem('currency')}`
                        ) : (
                            'No user data'
                        )}
                    </span>
                    <button
                        onClick={() => navigate(`/portfolio-stats/${selectedPortfolioId}`)}
                        disabled={!selectedPortfolioId}
                    >
                        View Portfolio
                    </button>
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
                            <button
                                className="dashboard-button buy"
                                onClick={() => openModal('buy')}
                                disabled={loadP || price == null || txLoading || !selectedPortfolioId}
                            >
                                {txLoading && showModal === 'buy' ? 'Processing...' : 'BUY'}
                            </button>
                            <button
                                className="dashboard-button sell"
                                onClick={() => openModal('sell')}
                                disabled={loadP || price == null || txLoading || !selectedPortfolioId}
                            >
                                {txLoading && showModal === 'sell' ? 'Processing...' : 'SELL'}
                            </button>
                        </div>
                    </section>
                </main>
            </div>

            <Modal
                isOpen={showModal !== null}
                title={`${showModal === 'buy' ? 'Buy' : 'Sell'} ${selected}`}
                onClose={closeModal}
            >
                <div className="modal-body">
                    <p>Current Price: {price} {sessionStorage.getItem('currency')}</p>
                    <label>
                        Number of Shares:
                        <input
                            type="number"
                            value={shares}
                            onChange={e => setShares(e.target.value ? Number(e.target.value) : '')}
                            min="1"
                            step="1"
                            placeholder="Enter number of shares"
                        />
                    </label>
                    <label>
                        Total {showModal === 'buy' ? 'Cost' : 'Proceeds'}:
                        <span>
                            {shares && price
                                ? (shares * price).toFixed(2) + ' ' + sessionStorage.getItem('currency')
                                : 'N/A'}
                        </span>
                    </label>
                    <label>
                        Notes:
                        <textarea
                            value={notes}
                            onChange={e => setNotes(e.target.value)}
                            placeholder="Add notes (optional)"
                        />
                    </label>
                    <div className="modal-actions">
                        <button
                            onClick={() => showModal && handleTransaction(showModal)}
                            disabled={txLoading}
                        >
                            {txLoading ? 'Processing...' : 'Confirm'}
                        </button>
                        <button onClick={closeModal} disabled={txLoading}>
                            Cancel
                        </button>
                    </div>
                </div>
            </Modal>
        </div>
    );
}
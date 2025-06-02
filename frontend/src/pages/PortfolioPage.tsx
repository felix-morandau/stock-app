import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {usePortfolios} from "../hooks/usePortfolios.tsx";
import {APP_NAME} from "../constants/merch.ts";

export default function PortfolioStatsPage() {
    const { portfolioId } = useParams<{ portfolioId: string }>();
    const navigate = useNavigate();

    const { portfolios, loading, error, fetchPortfolioStats } = usePortfolios();

    const [selectedPortfolio, setSelectedPortfolio] = useState<any>(null);

    useEffect(() => {
        if (portfolioId) {
            fetchPortfolioStats(portfolioId);
            const portfolio = portfolios?.find(p => p.id === portfolioId);
            setSelectedPortfolio(portfolio);
        }
    }, [portfolioId, portfolios, fetchPortfolioStats]);

    if (loading) return <div>Loading portfolio stats...</div>;
    if (error) return <div>Error loading portfolio stats: {error.message}</div>;
    if (!selectedPortfolio) return <div>No portfolio data available</div>;

    return (
        <div className="page-container">
            <nav className="navbar">
                <div className="logo">
                    <span>📊 {APP_NAME}</span>
                </div>
                <div className="nav-actions">
                    <button
                        className="nav-btn"
                        onClick={() => navigate('/client-dashboard')}
                    >
                        Back to Dashboard
                    </button>
                </div>
            </nav>

            <div className="portfolio-stats-container">
                <h2>Portfolio Statistics: {selectedPortfolio.name}</h2>
                <div className="stats-grid">
                    <div className="stat-item">
                        <span className="stat-label">Total Value:</span>
                        <span className="stat-value">
                            {selectedPortfolio.totalValue?.toFixed(2) || 'N/A'} {sessionStorage.getItem('currency')}
                        </span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-label">Total Invested:</span>
                        <span className="stat-value">
                            {selectedPortfolio.totalInvested?.toFixed(2) || 'N/A'} {sessionStorage.getItem('currency')}
                        </span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-label">Realized P&L:</span>
                        <span className="stat-value">
                            {selectedPortfolio.realizedPnl?.toFixed(2) || 'N/A'} {sessionStorage.getItem('currency')}
                        </span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-label">Unrealized P&L:</span>
                        <span className="stat-value">
                            {selectedPortfolio.unrealizedPnl?.toFixed(2) || 'N/A'} {sessionStorage.getItem('currency')}
                        </span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-label">Return Percentage:</span>
                        <span className="stat-value">
                            {selectedPortfolio.returnPercentage?.toFixed(2) || 'N/A'}%
                        </span>
                    </div>
                </div>

                <h3>Stock Holdings</h3>
                <div className="holdings-grid">
                    {selectedPortfolio.holdings?.map((holding: any) => (
                        <div key={holding.stockName} className="holding-item">
                            <span>{holding.stockName}</span>
                            <span>Shares: {holding.numberOfShares}</span>
                            <span>Avg Cost: {holding.averageCost?.toFixed(2)} {sessionStorage.getItem('currency')}</span>
                            <span>Current Price: {holding.currentPrice?.toFixed(2)} {sessionStorage.getItem('currency')}</span>
                            <span>Current Value: {holding.currentValue?.toFixed(2)} {sessionStorage.getItem('currency')}</span>
                            <span>Unrealized P&L: {holding.unrealizedPnl?.toFixed(2)} {sessionStorage.getItem('currency')}</span>
                        </div>
                    )) || <p>No holdings available</p>}
                </div>
            </div>
        </div>
    );
}
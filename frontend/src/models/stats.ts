export interface StockHoldingDTO {
    stockName: string;
    numberOfShares: number;
    averageCost: number;
    currentPrice: number;
    currentValue: number;
    unrealizedPnl: number;
}

export interface PortfolioStatsDTO {
    totalValue: number;
    totalInvested: number;
    realizedPnl: number;
    unrealizedPnl: number;
    returnPercentage: number;
    holdings: StockHoldingDTO[];
}
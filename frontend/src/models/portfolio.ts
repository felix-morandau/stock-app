import type Transaction from "./transaction.ts";

export interface Portfolio {
    id: string;
    name: string;
    amount: number;
    totalInvested: number;
    transactions: Transaction[];
    realizedPnl: number;
    unrealizedPnl: number;
    returnPercentage: number;
}
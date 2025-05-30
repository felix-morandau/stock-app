export type TransactionType = 'BUY' | 'SELL' | 'DEPOSIT' | 'WITHDRAW'

export interface CreateTransactionDTO {
    transactionType: TransactionType
    notes: string
    stockName: string
    sharePrice: number
    nrOfShares: number
}

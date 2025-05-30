import React, { useState } from 'react'
import './TransactionModal.css'
import type { CreateTransactionDTO, TransactionType } from '../models/createTransactionDTO'

interface TransactionModalProps {
    isOpen: boolean
    onClose: () => void
    onSubmit: (dto: CreateTransactionDTO) => void
    transactionType: TransactionType
    stockName: string
}

export default function TransactionModal({
                                             isOpen,
                                             onClose,
                                             onSubmit,
                                             transactionType,
                                             stockName
                                         }: TransactionModalProps) {
    const [notes, setNotes] = useState('')
    const [sharePrice, setSharePrice] = useState<number | ''>('')
    const [nrOfShares, setNrOfShares] = useState<number | ''>('')

    if (!isOpen) return null

    function handleSubmit(e: React.FormEvent) {
        e.preventDefault()
        if (
            sharePrice === '' ||
            nrOfShares === '' ||
            Number(sharePrice) <= 0 ||
            Number(nrOfShares) <= 0
        ) {
            alert('Please enter valid positive numbers')
            return
        }
        onSubmit({
            transactionType,
            notes,
            stockName,
            sharePrice: Number(sharePrice),
            nrOfShares: Number(nrOfShares)
        })
        // reset
        setNotes('')
        setSharePrice('')
        setNrOfShares('')
    }

    return (
        <div className="modal-overlay">
            <div className="modal">
                <h3>{transactionType} {stockName}</h3>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Share Price</label>
                        <input
                            type="number"
                            step="0.01"
                            value={sharePrice}
                            onChange={e => setSharePrice(e.target.value === '' ? '' : Number(e.target.value))}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Number of Shares</label>
                        <input
                            type="number"
                            value={nrOfShares}
                            onChange={e => setNrOfShares(e.target.value === '' ? '' : Number(e.target.value))}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Notes</label>
                        <textarea
                            value={notes}
                            onChange={e => setNotes(e.target.value)}
                        />
                    </div>
                    <div className="modal-buttons">
                        <button type="submit" className={`dashboard-button ${transactionType.toLowerCase()}`}>
                            Confirm {transactionType}
                        </button>
                        <button type="button" className="dashboard-button cancel" onClick={onClose}>
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

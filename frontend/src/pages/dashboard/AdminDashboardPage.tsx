import { useState } from 'react'
import GenericTable from '../../components/Table.tsx'
import type { TableColumn } from 'react-data-table-component'
import { useUsers } from '../../hooks/useUsers'
import { useTransactions } from '../../hooks/useTransactions'
import './AdminDashboardPage.css'
import type User from "../../models/user.ts";
import type Transaction from "../../models/transaction.ts";

type View = 'users' | 'transactions'

export default function AdminDashboardPage() {
    const [view, setView] = useState<View>('users')

    const {
        users,
        loading: usersLoading,
        error: usersError,
        deleteUser
    } = useUsers()

    const {
        transactions,
        loading: txLoading,
        error: txError
    } = useTransactions()

    const userColumns: TableColumn<User>[] = [
        { name: 'ID', selector: row => row.id, sortable: true },
        { name: 'First name', selector: row => row.firstName, sortable: true },
        { name: 'Last name', selector: row => row.lastName, sortable: true },
        { name: 'Email', selector: row => row.email, sortable: true },
        { name: 'Role', selector: row => row.type, sortable: true },
        {
            name: 'Actions',
            cell: row => (
                <button
                    onClick={() => {
                        if (window.confirm('Delete user?')) deleteUser(row.id)
                    }}
                >
                    Delete
                </button>
            )
        }
    ]

    const txnColumns: TableColumn<Transaction>[] = [
        { name: 'ID', selector: row => row.id, sortable: true },
        { name: 'User ID', selector: row => row.userId, sortable: true },
        {
            name: 'Date',
            selector: row => new Date(row.date).toLocaleString(),
            sortable: true
        },
        { name: 'Type', selector: row => row.type, sortable: true },
        {
            name: 'Amount',
            selector: row => row.amount.toFixed(2),
            sortable: true
        },
        { name: 'Description', selector: row => row.description }
    ]

    return (
        <div className="admin-container">
            <aside className="sidebar">
                <ul>
                    <li
                        className={view === 'users' ? 'active' : ''}
                        onClick={() => setView('users')}
                    >
                        Users
                    </li>
                    <li
                        className={view === 'transactions' ? 'active' : ''}
                        onClick={() => setView('transactions')}
                    >
                        Transactions
                    </li>
                </ul>
            </aside>

            <main className="main">
                {view === 'users' ? (
                    <GenericTable<User>
                        title="All Users"
                        columns={userColumns}
                        data={users}
                        loading={usersLoading}
                        isError={!!usersError}
                        onRowSelected={() => {}}
                        theme="light"
                    />
                ) : (
                    <GenericTable<Transaction>
                        title="All Transactions"
                        columns={txnColumns}
                        data={transactions}
                        loading={txLoading}
                        isError={!!txError}
                        onRowSelected={() => {}}
                        theme="light"
                    />
                )}
            </main>
        </div>
    )
}

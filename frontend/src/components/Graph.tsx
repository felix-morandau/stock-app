// src/components/Graph.tsx
import React from 'react'
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    Legend
} from 'recharts'
import type { StockData } from '../models/stockData'

interface GraphProps {
    title: string
    data: StockData[]
}

export default function Graph({ title, data }: GraphProps) {
    if (!data || data.length === 0) return <p>No data to display</p>

    const includeTime = data[0].date.includes('T') && !data[0].date.endsWith('T00:00')

    const chartData = data.map(({ date, candle }) => {
        const d = new Date(date)
        return {
            label: includeTime
                ? d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
                : d.toLocaleDateString(),
            open: candle.open,
            close: candle.close,
        }
    })

    return (
        <div style={{ width: '100%', height: 300, marginBottom: 20 }}>
            <h3>{title}</h3>
            <ResponsiveContainer>
                <LineChart data={chartData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis
                        dataKey="label"
                        interval={Math.floor(chartData.length / 10) || 0}
                        textAnchor="end"
                    />
                    <YAxis domain={['auto', 'auto']} />
                    <Tooltip
                        labelFormatter={(label: string) => includeTime ? `Time: ${label}` : `Date: ${label}`}
                        formatter={(value: number, name: string) => [
                            value.toFixed(2),
                            name.charAt(0).toUpperCase() + name.slice(1)
                        ]}
                    />
                    <Legend />
                    <Line
                        type="monotone"
                        dataKey="close"
                        dot={false}
                        name="Close"
                    />
                    <Line
                        type="monotone"
                        stroke="#8884d8"
                        dataKey="open"
                        dot={false}
                        name="Open"
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    )
}

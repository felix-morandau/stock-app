import { useState } from 'react';
import axios from 'axios';

function ClientDashboardPage() {
    const [symbol, setSymbol] = useState('META');
    const [monthlyData, setMonthlyData] = useState(null);
    const [dailyData, setDailyData] = useState(null);
    const [currentPrice, setCurrentPrice] = useState(null);
    const [selectedDate, setSelectedDate] = useState('');

    const apiBase = 'http://localhost:8080/stocks';

    const getToken = () => sessionStorage.getItem('token');

    const authHeaders = {
        headers: {
            Authorization: `Bearer ${getToken()}`
        }
    };

    const fetchMonthly = async () => {
        try {
            const res = await axios.get(`${apiBase}/${symbol}/monthly`, authHeaders);
            setMonthlyData(res.data);
        } catch (err) {
            console.error('Error fetching monthly data:', err);
        }
    };

    const fetchDaily = async (date = null) => {
        try {
            const url = date
                ? `${apiBase}/${symbol}/daily?date=${date}`
                : `${apiBase}/${symbol}/daily`;
            const res = await axios.get(url, authHeaders);
            setDailyData(res.data);
        } catch (err) {
            console.error('Error fetching daily data:', err);
        }
    };

    const fetchCurrent = async () => {
        try {
            const res = await axios.get(`${apiBase}/${symbol}/current`, authHeaders);
            setCurrentPrice(res.data);
        } catch (err) {
            console.error('Error fetching current price:', err);
        }
    };

    return (
        <div style={{ padding: 20 }}>
            <h2>Stock Dashboard for {symbol}</h2>

            <div style={{ marginBottom: 10 }}>
                <button onClick={fetchMonthly}>Fetch Monthly Data</button>
                <button onClick={() => fetchDaily()}>Fetch Daily Data (Today)</button>
                <input
                    type="date"
                    value={selectedDate}
                    onChange={e => setSelectedDate(e.target.value)}
                />
                <button onClick={() => fetchDaily(selectedDate)}>Fetch Daily (Pick Date)</button>
                <button onClick={fetchCurrent}>Fetch Current Price</button>
            </div>

            <div>
                {monthlyData && (
                    <div>
                        <h3>Monthly Data</h3>
                        <pre>{JSON.stringify(monthlyData, null, 2)}</pre>
                    </div>
                )}

                {dailyData && (
                    <div>
                        <h3>Daily Data</h3>
                        <pre>{JSON.stringify(dailyData, null, 2)}</pre>
                    </div>
                )}

                {currentPrice !== null && (
                    <div>
                        <h3>Current Price</h3>
                        <p>{currentPrice}</p>
                    </div>
                )}
            </div>
        </div>
    );
}

export default ClientDashboardPage;

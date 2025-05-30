import React, { useState } from "react";
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { API_BASE_URL } from "../../constants/api.ts";
import "./Auth.css";

interface LoginResponse {
    userId: string;
    role: string;
    token: string;
    errorMessage?: string;
}

interface UserSettings {
    language: string;
    currency: string;
    theme: string;
}

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    function getAuthHeaders(token: string) {
        return { headers: { Authorization: `Bearer ${token}` } };
    }

    function handleLogin(event: React.FormEvent) {
        event.preventDefault();
        setErrorMessage('');

        axios
            .post<LoginResponse>(
                `${API_BASE_URL}/login`,
                { email, password },
                { headers: { 'Content-Type': 'application/json' } }
            )
            .then(({ data: loginResponse }) => {
                const { token, userId, role } = loginResponse;

                sessionStorage.setItem('token', token);
                sessionStorage.setItem('userId', userId);
                sessionStorage.setItem('role', role);

                // fetch user settings after login
                axios
                    .get<UserSettings>(
                        `${API_BASE_URL}/settings/${userId}`,
                        getAuthHeaders(token)
                    )
                    .then(({ data: settings }) => {
                        sessionStorage.setItem('language', settings.language);
                        sessionStorage.setItem('currency', settings.currency);
                        sessionStorage.setItem('theme', settings.theme);
                    })
                    .catch(err => {
                        console.error('Failed to load settings:', err);
                    })
                    .finally(() => {
                        if (role === 'ADMIN') {
                            navigate('/admin-dashboard');
                        } else {
                            navigate('/client-dashboard');
                        }
                    });
            })
            .catch(error => {
                if (axios.isAxiosError(error) && error.response?.status === 401) {
                    console.error('Login failed:', error.response.data);
                    setErrorMessage(error.response.data.errorMessage || 'Unauthorized');
                } else {
                    console.error('An unexpected error occurred:', error);
                    setErrorMessage('Failed to login. Please try again later.');
                }
            });
    }

    return (
        <div className="login-container">
            <h1>Login</h1>
            <form onSubmit={handleLogin}>
                <div>
                    <label htmlFor="email">Email:</label>
                    <input
                        type="text"
                        id="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                    />
                </div>
                {errorMessage && <p className="error-message">{errorMessage}</p>}
                <button className="login" type="submit">Login</button>
                <div className="forgot-password-label">
                    <button type="button" onClick={() => navigate('/forgot-password')}>
                        Forgot Password?
                    </button>
                </div>
            </form>
            <div className="register-label">
                <p>Don't have an account?</p>
                <button type="button" onClick={() => navigate('/register')}>
                    Register
                </button>
            </div>
        </div>
    );
}

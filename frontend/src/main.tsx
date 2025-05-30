import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import LoginPage from './pages/login/LoginPage.tsx';
import RegisterPage from "./pages/login/RegisterPage.tsx";
import ClientDashboardPage from "./pages/dashboard/ClientDashboardPage.tsx";
import AdminDashboardPage from "./pages/dashboard/AdminDashboardPage.tsx";
import SettingsPage from "./pages/settings/SettingsPage.tsx";


createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Router>
        <Routes>
            <Route path="/" element={<Navigate to="/login" />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/client-dashboard" element={<ClientDashboardPage />} />
            <Route path="/admin-dashboard" element={<AdminDashboardPage />} />
            <Route path="/client-settings" element={<SettingsPage />} />
        </Routes>
    </Router>

  </StrictMode>,
)

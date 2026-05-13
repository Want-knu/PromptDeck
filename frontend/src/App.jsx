import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import DashboardPage from './pages/DashboardPage'
import ProvidersPage from './pages/ProvidersPage'
import ProviderSettingsPage from './pages/ProviderSettingsPage'
import ExecutionPage from './pages/ExecutionPage'
import HistoryPage from './pages/HistoryPage'
import PrivateRoute from './components/PrivateRoute'

function Private({ children }) {
  return <PrivateRoute>{children}</PrivateRoute>
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/" element={<Private><DashboardPage /></Private>} />
        <Route path="/providers" element={<Private><ProvidersPage /></Private>} />
        <Route path="/provider-settings" element={<Private><ProviderSettingsPage /></Private>} />
        <Route path="/execution" element={<Private><ExecutionPage /></Private>} />
        <Route path="/history" element={<Private><HistoryPage /></Private>} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

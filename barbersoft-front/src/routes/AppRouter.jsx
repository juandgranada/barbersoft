import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import ReservaPage from '@/pages/public/ReservaPage'
import LoginPage from '@/pages/admin/LoginPage'

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<ReservaPage />} />
        <Route path="/admin/login" element={<LoginPage />} />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  )
}
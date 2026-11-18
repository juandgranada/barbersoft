import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import ReservaPage from '@/pages/public/ReservaPage'
import LoginPage from '@/pages/admin/LoginPage'
import DashboardPage from '@/pages/admin/dashboard/DashboardPage'
import ReservasPage from '@/pages/admin/reservas/ReservasPage'
import BarberosPage from '@/pages/admin/barberos/BarberosPage'
import ServiciosPage from '@/pages/admin/servicios/ServiciosPage'
import HorariosPage from '@/pages/admin/horarios/HorariosPage'
import FestivosPage from '@/pages/admin/festivos/FestivosPage'
import AdminLayout from '@/components/layout/AdminLayout'
import ProtectedRoute from '@/components/layout/ProtectedRoute'

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<ReservaPage />} />
        <Route path="/admin/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<DashboardPage />} />
            <Route path="reservas" element={<ReservasPage />} />
            <Route path="barberos" element={<BarberosPage />} />
            <Route path="servicios" element={<ServiciosPage />} />
            <Route path="horarios" element={<HorariosPage />} />
            <Route path="festivos" element={<FestivosPage />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
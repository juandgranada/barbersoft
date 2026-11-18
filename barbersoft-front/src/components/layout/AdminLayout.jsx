import { useState } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { CalendarDays, Scissors, Users, Clock3, CalendarOff, LayoutDashboard, Menu, LogOut, X } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'

const navigation = [
  { to: '/admin', label: 'Resumen', icon: LayoutDashboard, end: true },
  { to: '/admin/reservas', label: 'Reservas', icon: CalendarDays },
  { to: '/admin/barberos', label: 'Barberos', icon: Users },
  { to: '/admin/servicios', label: 'Servicios', icon: Scissors },
  { to: '/admin/horarios', label: 'Horarios', icon: Clock3 },
  { to: '/admin/festivos', label: 'Festivos', icon: CalendarOff },
]

export default function AdminLayout() {
  const [open, setOpen] = useState(false)
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/admin/login', { replace: true })
  }

  return <div className="admin-shell">
    <aside className={`sidebar ${open ? 'sidebar-open' : ''}`}>
      <div className="sidebar-brand">
        <div className="brand-mark"><Scissors size={20} /></div>
        <strong>BarberSoft</strong>
        <button className="icon-button sidebar-close" onClick={() => setOpen(false)} aria-label="Cerrar menú"><X size={20} /></button>
      </div>
      <nav className="sidebar-nav">
        {navigation.map(({ to, label, icon: Icon, end }) => <NavLink key={to} to={to} end={end} className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} onClick={() => setOpen(false)}>
          <Icon size={18} /><span>{label}</span>
        </NavLink>)}
      </nav>
      <div className="sidebar-footer">
        <div className="admin-user"><span>{user?.nombre?.[0] || 'A'}</span><div><strong>{user?.nombre || 'Administrador'}</strong><small>{user?.email}</small></div></div>
        <button className="nav-item logout-button" onClick={handleLogout}><LogOut size={18} /><span>Cerrar sesión</span></button>
      </div>
    </aside>
    {open && <button className="sidebar-overlay" aria-label="Cerrar menú" onClick={() => setOpen(false)} />}
    <main className="admin-main">
      <header className="admin-topbar"><button className="icon-button menu-button" onClick={() => setOpen(true)} aria-label="Abrir menú"><Menu size={20} /></button><span>Panel administrativo</span></header>
      <div className="admin-content"><Outlet /></div>
    </main>
  </div>
}

import { useEffect, useState } from 'react'
import { CalendarDays, CalendarCheck, Users, Scissors } from 'lucide-react'
import { getReservas } from '@/api/reservasApi'
import { getBarberos } from '@/api/barberosApi'
import { getServicios } from '@/api/serviciosApi'
import { Card, LoadingBlock, PageHeader } from '@/components/ui/UI'

export default function DashboardPage() {
  const [loading, setLoading] = useState(true)
  const [stats, setStats] = useState({ reservas: 0, barberos: 0, servicios: 0 })

  useEffect(() => {
    Promise.all([getReservas(), getBarberos(), getServicios()]).then(([reservas, barberos, servicios]) => {
      setStats({ reservas: reservas.data.length, barberos: barberos.data.length, servicios: servicios.data.length })
    }).finally(() => setLoading(false))
  }, [])

  const cards = [
    { label: 'Reservas', value: stats.reservas, icon: CalendarDays, tone: 'gold' },
    { label: 'Confirmadas', value: stats.reservas, icon: CalendarCheck, tone: 'blue' },
    { label: 'Barberos', value: stats.barberos, icon: Users, tone: 'green' },
    { label: 'Servicios', value: stats.servicios, icon: Scissors, tone: 'rose' },
  ]

  return <><PageHeader title="Resumen" description="Vista general de tu barbería" />{loading ? <LoadingBlock /> : <div className="stats-grid">
    {cards.map(({ label, value, icon: Icon, tone }) => <Card className="stat-card" key={label}><div className={`stat-icon ${tone}`}><Icon size={21} /></div><div><span>{label}</span><strong>{value}</strong></div></Card>)}
  </div>}</>
}

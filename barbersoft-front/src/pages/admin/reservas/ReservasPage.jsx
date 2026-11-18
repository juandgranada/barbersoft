import { useEffect, useState } from 'react'
import { CalendarDays } from 'lucide-react'
import { cambiarEstadoReserva, getReservas } from '@/api/reservasApi'
import { getBarberos } from '@/api/barberosApi'
import { Alert, Badge, Card, LoadingBlock, PageHeader } from '@/components/ui/UI'
import { dateTime, getErrorMessage, money } from '@/utils/format'

const tone = { CONFIRMADA: 'blue', CANCELADA: 'red', COMPLETADA: 'green' }

export default function ReservasPage() {
  const [reservas, setReservas] = useState([])
  const [barberos, setBarberos] = useState([])
  const [filters, setFilters] = useState({ fecha: '', barberoId: '', estado: '' })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = async () => {
    setLoading(true); setError('')
    try {
      const params = Object.fromEntries(Object.entries(filters).filter(([, value]) => value))
      const [reservasResponse, barberosResponse] = await Promise.all([getReservas(params), getBarberos()])
      setReservas(reservasResponse.data); setBarberos(barberosResponse.data)
    } catch (errorValue) { setError(getErrorMessage(errorValue)) }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  const updateState = async (id, estado) => {
    try { await cambiarEstadoReserva(id, estado); await load() } catch (errorValue) { setError(getErrorMessage(errorValue)) }
  }

  return <><PageHeader title="Reservas" description="Gestiona las citas confirmadas de la barbería" />
    <div className="filters-row">
      <input className="input" type="date" value={filters.fecha} onChange={(event) => setFilters({ ...filters, fecha: event.target.value })} />
      <select className="input" value={filters.barberoId} onChange={(event) => setFilters({ ...filters, barberoId: event.target.value })}><option value="">Todos los barberos</option>{barberos.map((item) => <option key={item.id} value={item.id}>{item.nombre}</option>)}</select>
      <select className="input" value={filters.estado} onChange={(event) => setFilters({ ...filters, estado: event.target.value })}><option value="">Todos los estados</option><option>CONFIRMADA</option><option>CANCELADA</option><option>COMPLETADA</option></select>
      <button className="btn btn-primary" onClick={load}>Filtrar</button>
    </div>
    <Alert message={error} />{loading ? <LoadingBlock /> : reservas.length ? <div className="entity-grid">{reservas.map((item) => <Card className="reservation-card" key={item.id}>
      <div className="reservation-head"><span className="entity-icon"><CalendarDays size={20} /></span><Badge tone={tone[item.estado]}>{item.estado}</Badge></div>
      <h2>{item.nombreCliente}</h2><p>{item.nombreBarbero}</p><strong>{money(item.total)}</strong>
      <small>{dateTime(item.fechaInicio)} · {item.codigoReserva}</small>
      {item.estado === 'CONFIRMADA' && <div className="card-actions"><button className="btn btn-sm btn-success" onClick={() => updateState(item.id, 'COMPLETADA')}>Completar</button><button className="btn btn-sm btn-danger" onClick={() => updateState(item.id, 'CANCELADA')}>Cancelar</button></div>}
    </Card>)}</div> : <Card className="empty-state">No hay reservas para los filtros seleccionados.</Card>}
  </>
}

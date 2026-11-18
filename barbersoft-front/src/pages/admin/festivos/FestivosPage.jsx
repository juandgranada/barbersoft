import { useEffect, useState } from 'react'
import { Plus, CalendarOff, Pencil } from 'lucide-react'
import { createFestivo, getFestivos, updateFestivo, updateFestivoEstado } from '@/api/festivosApi'
import { Alert, Badge, Button, Card, Input, LoadingBlock, PageHeader } from '@/components/ui/UI'
import { getErrorMessage } from '@/utils/format'

export default function FestivosPage() {
  const [items, setItems] = useState([]); const [form, setForm] = useState({ nombre: '', fecha: '' }); const [editing, setEditing] = useState(null); const [open, setOpen] = useState(false); const [loading, setLoading] = useState(true); const [error, setError] = useState('')
  const load = async () => { setLoading(true); try { setItems((await getFestivos()).data) } catch (e) { setError(getErrorMessage(e)) } finally { setLoading(false) } }
  useEffect(() => { load() }, [])
  const save = async (event) => { event.preventDefault(); try { editing ? await updateFestivo(editing, form) : await createFestivo(form); setOpen(false); setEditing(null); setForm({ nombre: '', fecha: '' }); await load() } catch (e) { setError(getErrorMessage(e)) } }
  const toggle = async (item) => { try { await updateFestivoEstado(item.id, !item.activo); await load() } catch (e) { setError(getErrorMessage(e)) } }
  return <><PageHeader title="Festivos" description="Administra los días sin atención" action={<Button onClick={() => { setEditing(null); setForm({ nombre: '', fecha: '' }); setOpen(true) }}><Plus size={16} /> Nuevo festivo</Button>} /><Alert message={error} />
    {open && <Card className="entity-form"><h2>{editing ? 'Editar festivo' : 'Nuevo festivo'}</h2><form onSubmit={save}><div className="form-grid"><Input label="Nombre" value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required /><Input label="Fecha" type="date" value={form.fecha} onChange={(e) => setForm({ ...form, fecha: e.target.value })} required /></div><div className="form-actions"><Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancelar</Button><Button type="submit">Guardar</Button></div></form></Card>}
    {loading ? <LoadingBlock /> : <div className="entity-grid">{items.map((item) => <Card className="entity-card" key={item.id}><div className="entity-card-head"><span className="entity-icon"><CalendarOff size={20} /></span><Badge tone={item.activo ? 'green' : 'neutral'}>{item.activo ? 'Activo' : 'Inactivo'}</Badge></div><h2>{item.nombre}</h2><strong>{item.fecha}</strong><div className="card-actions"><button className="btn btn-sm btn-secondary" onClick={() => { setEditing(item.id); setForm({ nombre: item.nombre, fecha: item.fecha }); setOpen(true) }}><Pencil size={15} /> Editar</button><button className={`btn btn-sm ${item.activo ? 'btn-danger' : 'btn-success'}`} onClick={() => toggle(item)}>{item.activo ? 'Desactivar' : 'Activar'}</button></div></Card>)}</div>}
  </>
}

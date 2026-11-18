import { useEffect, useState } from 'react'
import { Plus, Scissors, Pencil } from 'lucide-react'
import { createServicio, getServicios, updateServicio, updateServicioEstado } from '@/api/serviciosApi'
import { Alert, Badge, Button, Card, Input, LoadingBlock, PageHeader, Textarea } from '@/components/ui/UI'
import { getErrorMessage, money } from '@/utils/format'

const empty = { nombre: '', descripcion: '', precio: '', duracionMinutos: '' }
export default function ServiciosPage() {
  const [items, setItems] = useState([]); const [form, setForm] = useState(empty); const [editing, setEditing] = useState(null); const [open, setOpen] = useState(false); const [loading, setLoading] = useState(true); const [error, setError] = useState('')
  const load = async () => { setLoading(true); try { setItems((await getServicios()).data) } catch (e) { setError(getErrorMessage(e)) } finally { setLoading(false) } }
  useEffect(() => { load() }, [])
  const save = async (event) => { event.preventDefault(); setError(''); try { const body = { ...form, precio: Number(form.precio), duracionMinutos: Number(form.duracionMinutos) }; editing ? await updateServicio(editing, body) : await createServicio(body); setOpen(false); setEditing(null); setForm(empty); await load() } catch (e) { setError(getErrorMessage(e)) } }
  const edit = (item) => { setEditing(item.id); setForm({ nombre: item.nombre, descripcion: item.descripcion || '', precio: item.precio, duracionMinutos: item.duracionMinutos }); setOpen(true) }
  const toggle = async (item) => { try { await updateServicioEstado(item.id, !item.activo); await load() } catch (e) { setError(getErrorMessage(e)) } }
  return <><PageHeader title="Servicios" description="Gestiona precios y duraciones" action={<Button onClick={() => { setEditing(null); setForm(empty); setOpen(true) }}><Plus size={16} /> Nuevo servicio</Button>} /><Alert message={error} />
    {open && <Card className="entity-form"><h2>{editing ? 'Editar servicio' : 'Nuevo servicio'}</h2><form onSubmit={save}><div className="form-grid"><Input label="Nombre" value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required /><Input label="Precio" type="number" min="1" step="0.01" value={form.precio} onChange={(e) => setForm({ ...form, precio: e.target.value })} required /><Input label="Duración (min)" type="number" min="1" value={form.duracionMinutos} onChange={(e) => setForm({ ...form, duracionMinutos: e.target.value })} required /></div><Textarea label="Descripción" value={form.descripcion} onChange={(e) => setForm({ ...form, descripcion: e.target.value })} /><div className="form-actions"><Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancelar</Button><Button type="submit">Guardar</Button></div></form></Card>}
    {loading ? <LoadingBlock /> : <div className="entity-grid">{items.map((item) => <Card className="entity-card" key={item.id}><div className="entity-card-head"><span className="entity-icon"><Scissors size={20} /></span><Badge tone={item.activo ? 'green' : 'neutral'}>{item.activo ? 'Activo' : 'Inactivo'}</Badge></div><h2>{item.nombre}</h2><p>{item.descripcion || 'Sin descripción'}</p><strong>{money(item.precio)}</strong><small>{item.duracionMinutos} minutos</small><div className="card-actions"><button className="btn btn-sm btn-secondary" onClick={() => edit(item)}><Pencil size={15} /> Editar</button><button className={`btn btn-sm ${item.activo ? 'btn-danger' : 'btn-success'}`} onClick={() => toggle(item)}>{item.activo ? 'Desactivar' : 'Activar'}</button></div></Card>)}</div>}
  </>
}

import { useEffect, useState } from 'react'
import { Plus, Users, Pencil, Scissors } from 'lucide-react'
import { createBarbero, getBarberos, updateBarbero, updateBarberoEstado } from '@/api/barberosApi'
import { getServiciosActivos, getServiciosAsignados, asignarServicio, desasignarServicio } from '@/api/serviciosApi'
import { Alert, Badge, Button, Card, Input, LoadingBlock, PageHeader, Textarea } from '@/components/ui/UI'
import { getErrorMessage } from '@/utils/format'

const empty = { nombre: '', apellido: '', telefono: '', email: '', foto: '', descripcion: '', activo: true }

export default function BarberosPage() {
  const [items, setItems] = useState([])
  const [form, setForm] = useState(empty)
  const [editing, setEditing] = useState(null)
  const [open, setOpen] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [servicios, setServicios] = useState([])
  const [serviciosAsignados, setServiciosAsignados] = useState([])

  const load = async () => {
    setLoading(true)
    try {
      const [barberosResponse, serviciosResponse] = await Promise.all([getBarberos(), getServiciosActivos()])
      setItems(barberosResponse.data)
      setServicios(serviciosResponse.data)
    } catch (errorValue) {
      setError(getErrorMessage(errorValue))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const save = async (event) => {
    event.preventDefault()
    setError('')
    try {
      const barberoResponse = editing
        ? await updateBarbero(editing, form)
        : await createBarbero(form)

      setOpen(false)
      setEditing(null)
      setForm(empty)
      setServiciosAsignados([])
      await load()

      if (!editing && barberoResponse.data?.id) {
        setEditing(barberoResponse.data.id)
        setOpen(true)
        setServiciosAsignados([])
      } else {
        setOpen(false)
        setEditing(null)
        setForm(empty)
        setServiciosAsignados([])
      }
    } catch (errorValue) {
      setError(getErrorMessage(errorValue))
    }
  }

  const edit = async (item) => {
    setEditing(item.id)
    setForm({
      nombre: item.nombre || '',
      apellido: item.apellido || '',
      telefono: item.telefono || '',
      email: item.email || '',
      foto: item.foto || '',
      descripcion: item.descripcion || '',
      activo: item.activo,
    })
    setOpen(true)

    try {
      const response = await getServiciosAsignados(item.id)
      setServiciosAsignados(response.data.map((asignacion) => asignacion.servicio.id))
    } catch (errorValue) {
      setServiciosAsignados([])
      setError(getErrorMessage(errorValue))
    }
  }

  const toggleServicio = async (servicioId) => {
    if (!editing) {
      setError('Guarda el barbero antes de asignarle servicios.')
      return
    }

    setError('')
    try {
      if (serviciosAsignados.includes(servicioId)) {
        await desasignarServicio(editing, servicioId)
        setServiciosAsignados((current) => current.filter((id) => id !== servicioId))
      } else {
        await asignarServicio({ barberoId: editing, servicioId })
        setServiciosAsignados((current) => [...current, servicioId])
      }
    } catch (errorValue) {
      setError(getErrorMessage(errorValue))
    }
  }

  const toggle = async (item) => {
    try {
      await updateBarberoEstado(item.id, !item.activo)
      await load()
    } catch (errorValue) {
      setError(getErrorMessage(errorValue))
    }
  }

  return <>
    <PageHeader
      title="Barberos"
      description="Administra el equipo y los servicios de cada barbero"
      action={<Button onClick={() => { setEditing(null); setForm(empty); setServiciosAsignados([]); setOpen(true) }}><Plus size={16} /> Nuevo barbero</Button>}
    />
    <Alert message={error} />

    {open && <Card className="entity-form">
      <h2>{editing ? 'Editar barbero' : 'Nuevo barbero'}</h2>
      <form onSubmit={save}>
        <div className="form-grid">
          <Input label="Nombre" value={form.nombre} onChange={(event) => setForm({ ...form, nombre: event.target.value })} required />
          <Input label="Apellido" value={form.apellido} onChange={(event) => setForm({ ...form, apellido: event.target.value })} />
          <Input label="Teléfono" value={form.telefono} onChange={(event) => setForm({ ...form, telefono: event.target.value })} />
          <Input label="Correo" type="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} />
          <Input label="URL de foto" value={form.foto} onChange={(event) => setForm({ ...form, foto: event.target.value })} />
        </div>
        <Textarea label="Descripción" value={form.descripcion} onChange={(event) => setForm({ ...form, descripcion: event.target.value })} />

        {editing && <div className="service-assignment">
          <h3>Servicios que realiza</h3>
          {servicios.length ? <div className="service-assignment-grid">
            {servicios.map((servicio) => {
              const assigned = serviciosAsignados.includes(servicio.id)
              return <button
                type="button"
                key={servicio.id}
                className={`assignment-option ${assigned ? 'selected' : ''}`}
                onClick={() => toggleServicio(servicio.id)}
              >
                <span className="assignment-icon"><Scissors size={16} /></span>
                <span><strong>{servicio.nombre}</strong><small>{servicio.duracionMinutos} min</small></span>
                <Badge tone={assigned ? 'green' : 'neutral'}>{assigned ? 'Asignado' : 'Sin asignar'}</Badge>
              </button>
            })}
          </div> : <p className="assignment-empty">Primero crea servicios activos.</p>}
        </div>}

        <div className="form-actions">
          <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancelar</Button>
          <Button type="submit">Guardar datos</Button>
        </div>
      </form>
    </Card>}

    {loading ? <LoadingBlock /> : <div className="entity-grid">
      {items.map((item) => <Card className="entity-card" key={item.id}>
        <div className="entity-card-head">
          <span className="entity-icon"><Users size={20} /></span>
          <Badge tone={item.activo ? 'green' : 'neutral'}>{item.activo ? 'Activo' : 'Inactivo'}</Badge>
        </div>
        <h2>{item.nombre} {item.apellido}</h2>
        <p>{item.email || 'Sin correo'}</p>
        <small>{item.telefono || 'Sin teléfono'}</small>
        <div className="card-actions">
          <button className="btn btn-sm btn-secondary" onClick={() => edit(item)}><Pencil size={15} /> Editar</button>
          <button className={`btn btn-sm ${item.activo ? 'btn-danger' : 'btn-success'}`} onClick={() => toggle(item)}>{item.activo ? 'Desactivar' : 'Activar'}</button>
        </div>
      </Card>)}
    </div>}
  </>
}

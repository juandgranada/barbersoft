import { useEffect, useState } from 'react'
import { Phone, Scissors, Clock3, CheckCircle2 } from 'lucide-react'
import { getBarberosActivos } from '@/api/barberosApi'
import { getServiciosActivos } from '@/api/serviciosApi'
import { consultarDisponibilidad, crearReserva } from '@/api/reservasApi'
import { Alert, Button, Input, Textarea } from '@/components/ui/UI'
import { BookingSection, StepIndicator } from './bookingSteps'
import { getErrorMessage, money } from '@/utils/format'

const initialCustomer = { nombreCliente: '', telefonoCliente: '', emailCliente: '', observaciones: '' }

export default function ReservaPage() {
  const [barberos, setBarberos] = useState([])
  const [servicios, setServicios] = useState([])
  const [barberoId, setBarberoId] = useState(null)
  const [serviciosIds, setServiciosIds] = useState([])
  const [fecha, setFecha] = useState('')
  const [hora, setHora] = useState('')
  const [horarios, setHorarios] = useState([])
  const [customer, setCustomer] = useState(initialCustomer)
  const [step, setStep] = useState(1)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(null)

  useEffect(() => {
    Promise.all([getBarberosActivos(), getServiciosActivos()]).then(([barberosResponse, serviciosResponse]) => {
      setBarberos(barberosResponse.data)
      setServicios(serviciosResponse.data)
    }).catch((errorValue) => setError(getErrorMessage(errorValue)))
  }, [])

  const toggleServicio = (id) => {
    setServiciosIds((current) => current.includes(id) ? current.filter((item) => item !== id) : [...current, id])
    setHora('')
  }

  const buscarHorarios = async () => {
    if (!barberoId || !fecha || !serviciosIds.length) return
    setLoading(true); setError(''); setHora('')
    try {
      const response = await consultarDisponibilidad({ barberoId, fecha, serviciosIds })
      setHorarios(response.data.horariosDisponibles || [])
      setStep(3)
    } catch (errorValue) { setError(getErrorMessage(errorValue)) }
    finally { setLoading(false) }
  }

  const confirmarReserva = async (event) => {
    event.preventDefault()
    setLoading(true); setError('')
    try {
      const response = await crearReserva({ ...customer, barberoId, fecha, horaInicio: hora, serviciosIds })
      setSuccess(response.data)
      window.scrollTo({ top: 0, behavior: 'smooth' })
    } catch (errorValue) { setError(getErrorMessage(errorValue)) }
    finally { setLoading(false) }
  }

  const seleccionados = servicios.filter((servicio) => serviciosIds.includes(servicio.id))
  const total = seleccionados.reduce((sum, servicio) => sum + Number(servicio.precio), 0)
  const duration = seleccionados.reduce((sum, servicio) => sum + Number(servicio.duracionMinutos), 0)
  const today = new Date().toISOString().slice(0, 10)

  if (success) return <main className="booking-page"><div className="booking-shell">
    <div className="success-card"><CheckCircle2 size={44} /><h1>¡Reserva confirmada!</h1><p>Tu código es <strong>{success.codigoReserva}</strong></p><p>{success.fechaInicio?.replace('T', ' · ')}</p>
      <Button onClick={() => window.location.reload()} variant="outline">Hacer otra reserva</Button></div>
  </div></main>

  return <main className="booking-page">
    <div className="booking-shell">
      <header className="booking-hero">
        <div className="booking-logo"><Scissors size={34} /></div>
        <div><h1>BarberSoft</h1><p>Estilo, precisión y atención personalizada</p></div>
        <a href="tel:+573001234567" className="booking-phone" aria-label="Llamar"><Phone size={22} /></a>
      </header>
      <div className="booking-content"><StepIndicator current={step} /><Alert message={error} />
        {step === 1 && <>
          <BookingSection step="1" title="Elige tu barbero">
            <div className="barber-grid">{barberos.map((barbero) => <button key={barbero.id} className={`barber-option ${barberoId === barbero.id ? 'selected' : ''}`} onClick={() => setBarberoId(barbero.id)}>
              <span className="barber-avatar">{barbero.foto ? <img src={barbero.foto} alt="" /> : barbero.nombre?.[0]}</span><strong>{barbero.nombre} {barbero.apellido}</strong>
            </button>)}</div>
          </BookingSection>
          <Button size="lg" className="booking-next" disabled={!barberoId} onClick={() => setStep(2)}>Continuar</Button>
        </>}

        {step === 2 && <>
          <BookingSection step="2" title="Selecciona los servicios">
            <div className="service-list">{servicios.map((servicio) => <button key={servicio.id} className={`service-option ${serviciosIds.includes(servicio.id) ? 'selected' : ''}`} onClick={() => toggleServicio(servicio.id)}>
              <span><strong>{servicio.nombre}</strong><small><Clock3 size={13} /> {servicio.duracionMinutos} min</small></span><b>{money(servicio.precio)}</b>
            </button>)}</div>
            {serviciosIds.length > 0 && <div className="booking-total"><span><small>Duración total</small><strong>{duration} min</strong></span><span><small>Total</small><strong>{money(total)}</strong></span></div>}
          </BookingSection>
          <div className="booking-date"><Input label="Fecha" type="date" min={today} value={fecha} onChange={(event) => setFecha(event.target.value)} /></div>
          <div className="booking-actions"><Button variant="outline" size="lg" onClick={() => setStep(1)}>Atrás</Button><Button size="lg" loading={loading} disabled={!serviciosIds.length || !fecha} onClick={buscarHorarios}>Ver horarios</Button></div>
        </>}

        {step === 3 && <>
          <BookingSection step="3" title="Selecciona una hora"><div className="time-grid">{horarios.length ? horarios.map((item) => <button key={item} className={hora === item ? 'selected' : ''} onClick={() => setHora(item)}>{item}</button>) : <p className="no-times">No hay horarios disponibles para esta fecha.</p>}</div></BookingSection>
          <div className="booking-actions"><Button variant="outline" size="lg" onClick={() => setStep(2)}>Atrás</Button><Button size="lg" disabled={!hora} onClick={() => setStep(4)}>Continuar</Button></div>
        </>}

        {step === 4 && <form onSubmit={confirmarReserva}>
          <BookingSection step="4" title="Tus datos">
            <div className="booking-summary">{fecha} · {hora} · {duration} min</div>
            <Input label="Nombre completo *" value={customer.nombreCliente} onChange={(event) => setCustomer({ ...customer, nombreCliente: event.target.value })} required />
            <Input label="Teléfono *" type="tel" value={customer.telefonoCliente} onChange={(event) => setCustomer({ ...customer, telefonoCliente: event.target.value.replace(/[^0-9()+\- ]/g, '').slice(0, 20) })} required />
            <Input label="Correo electrónico *" type="email" value={customer.emailCliente} onChange={(event) => setCustomer({ ...customer, emailCliente: event.target.value })} required />
            <Textarea label="Observaciones" value={customer.observaciones} onChange={(event) => setCustomer({ ...customer, observaciones: event.target.value })} maxLength={500} />
          </BookingSection>
          <div className="booking-actions"><Button type="button" variant="outline" size="lg" onClick={() => setStep(3)}>Atrás</Button><Button type="submit" size="lg" loading={loading} className="confirm-button">Confirmar reserva · {money(total)}</Button></div>
        </form>}
      </div>
    </div>
  </main>
}
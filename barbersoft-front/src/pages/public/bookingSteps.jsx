import { Check } from 'lucide-react'

export function StepIndicator({ current }) {
  const labels = ['Barbero', 'Servicios', 'Horario', 'Datos']
  return <div className="step-indicator" aria-label="Progreso de reserva">
    {labels.map((label, index) => {
      const step = index + 1
      return <div className="step-item" key={label}>
        <span className={`step-circle ${step < current ? 'complete' : ''} ${step === current ? 'active' : ''}`}>
          {step < current ? <Check size={16} /> : step}
        </span>
        {index < labels.length - 1 && <span className={`step-line ${step < current ? 'complete' : ''}`} />}
        <small>{label}</small>
      </div>
    })}
  </div>
}

export function BookingSection({ step, title, children }) {
  return <section className="booking-section">
    <header><span>{step}</span><h2>{title}</h2></header>
    {children}
  </section>
}

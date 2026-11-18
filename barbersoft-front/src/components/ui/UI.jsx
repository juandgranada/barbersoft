import { LoaderCircle } from 'lucide-react'

export function Button({ children, variant = 'primary', size = 'md', className = '', loading = false, ...props }) {
  return <button className={`btn btn-${variant} btn-${size} ${className}`} disabled={loading || props.disabled} {...props}>
    {loading && <LoaderCircle className="spinner" size={16} />}{children}
  </button>
}

export function Input({ label, error, className = '', ...props }) {
  return <label className={`field ${className}`}>
    {label && <span className="field-label">{label}</span>}
    <input className="input" {...props} />
    {error && <span className="field-error">{error}</span>}
  </label>
}

export function Select({ label, error, children, className = '', ...props }) {
  return <label className={`field ${className}`}>
    {label && <span className="field-label">{label}</span>}
    <select className="input" {...props}>{children}</select>
    {error && <span className="field-error">{error}</span>}
  </label>
}

export function Textarea({ label, error, className = '', ...props }) {
  return <label className={`field ${className}`}>
    {label && <span className="field-label">{label}</span>}
    <textarea className="input textarea" {...props} />
    {error && <span className="field-error">{error}</span>}
  </label>
}

export function Card({ children, className = '', ...props }) {
  return <section className={`card ${className}`} {...props}>{children}</section>
}

export function Badge({ children, tone = 'neutral' }) {
  return <span className={`badge badge-${tone}`}>{children}</span>
}

export function PageHeader({ title, description, action }) {
  return <header className="page-header">
    <div><h1>{title}</h1>{description && <p>{description}</p>}</div>
    {action}
  </header>
}

export function Alert({ message, tone = 'error' }) {
  if (!message) return null
  return <div className={`alert alert-${tone}`} role="alert">{message}</div>
}

export function EmptyState({ icon: Icon, title, description }) {
  return <div className="empty-state">{Icon && <Icon size={28} />}<strong>{title}</strong>{description && <p>{description}</p>}</div>
}

export function LoadingBlock() {
  return <div className="loading-block"><LoaderCircle className="spinner" size={22} /><span>Cargando...</span></div>
}

import { useState } from 'react'
import { useLocation, useNavigate, Link } from 'react-router-dom'
import { Scissors } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'
import { Alert, Button, Input } from '@/components/ui/UI'
import { getErrorMessage } from '@/utils/format'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const submit = async (event) => {
    event.preventDefault()
    setLoading(true)
    setError('')
    try {
      await login(form)
      navigate(location.state?.from?.pathname || '/admin', { replace: true })
    } catch (errorValue) {
      setError(getErrorMessage(errorValue))
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="login-page">
      <div className="login-decoration">
        <div className="login-orb" />
        <div className="login-grid" />
      </div>
      <form className="login-card" onSubmit={submit}>
        <Link to="/" className="login-brand" aria-label="Ir al inicio">
          <span>
            <Scissors size={30} />
          </span>
        </Link>
        <div className="login-heading">
          <h1>Bienvenido de nuevo</h1>
          <p>Ingresa a tu cuenta para continuar</p>
        </div>
        <Alert message={error} />
        <Input
          label="Correo electrónico"
          type="email"
          placeholder="tu@email.com"
          value={form.email}
          onChange={(event) => setForm({ ...form, email: event.target.value })}
          required
          autoComplete="email"
        />
        <Input
          label="Contraseña"
          type="password"
          placeholder="••••••••"
          value={form.password}
          onChange={(event) => setForm({ ...form, password: event.target.value })}
          required
          autoComplete="current-password"
        />
        <Button type="submit" size="lg" loading={loading} className="login-submit">
          Iniciar sesión
        </Button>
        <Link className="back-link" to="/">
          Volver a reservar una cita
        </Link>
      </form>
    </main>
  )
}
import axios from 'axios'

const isPublicRequest = (config) => {
  const url = config.url || ''
  return url === '/api/disponibilidad'
    || url === '/api/reservas'
    || url === '/api/auth/login'
    || url === '/api/barberos/activos'
    || url === '/api/servicios/activos'
}

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
})

client.interceptors.request.use((config) => {
  if (isPublicRequest(config)) {
    if (config.headers?.delete) {
      config.headers.delete('Authorization')
    } else {
      delete config.headers.Authorization
    }
    return config
  }

  const token = localStorage.getItem('barbersoft_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !isPublicRequest(error.config || {})) {
      localStorage.removeItem('barbersoft_token')
      localStorage.removeItem('barbersoft_user')
      if (window.location.pathname !== '/admin/login') {
        window.dispatchEvent(new CustomEvent('barbersoft:unauthorized'))
      }
    }
    return Promise.reject(error)
  },
)

export default client

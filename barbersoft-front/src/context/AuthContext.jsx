import { createContext, useContext, useEffect, useState } from 'react'
import { login as loginRequest } from '@/api/authApi'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('barbersoft_user')) } catch { return null }
  })

  useEffect(() => {
    const logout = () => setUser(null)
    window.addEventListener('barbersoft:unauthorized', logout)
    return () => window.removeEventListener('barbersoft:unauthorized', logout)
  }, [])

  const login = async (credentials) => {
    const response = await loginRequest(credentials)
    localStorage.setItem('barbersoft_token', response.data.token)
    localStorage.setItem('barbersoft_user', JSON.stringify(response.data))
    setUser(response.data)
    return response.data
  }

  const logout = () => {
    localStorage.removeItem('barbersoft_token')
    localStorage.removeItem('barbersoft_user')
    setUser(null)
  }

  return <AuthContext.Provider value={{ user, login, logout, isAuthenticated: Boolean(user) }}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)

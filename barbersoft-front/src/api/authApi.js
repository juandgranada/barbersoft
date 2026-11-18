import client from './client'

export const login = (credentials) => client.post('/api/auth/login', credentials)

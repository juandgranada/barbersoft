import api from './axios'

export const getBarberos = () => api.get('/api/barberos')
export const createBarbero = (data) => api.post('/barberos', data)
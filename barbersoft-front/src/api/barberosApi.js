import client from './client'

export const getBarberos = () => client.get('/api/barberos')
export const getBarberosActivos = () => client.get('/api/barberos/activos')
export const getBarbero = (id) => client.get(`/api/barberos/${id}`)
export const createBarbero = (body) => client.post('/api/barberos', body)
export const updateBarbero = (id, body) => client.put(`/api/barberos/${id}`, body)
export const updateBarberoEstado = (id, activo) => client.patch(`/api/barberos/${id}/estado`, null, { params: { activo } })
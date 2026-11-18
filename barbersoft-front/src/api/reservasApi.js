import client from './client'

export const consultarDisponibilidad = (body) => client.post('/api/disponibilidad', body)
export const crearReserva = (body) => client.post('/api/reservas', body)
export const getReservas = (params = {}) => client.get('/api/reservas', { params })
export const getReserva = (id) => client.get(`/api/reservas/${id}`)
export const cambiarEstadoReserva = (id, estado) => client.patch(`/api/reservas/${id}/estado`, { estado })

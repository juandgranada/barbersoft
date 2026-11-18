import client from './client'

export const getServicios = () => client.get('/api/servicios')
export const getServiciosActivos = () => client.get('/api/servicios/activos')
export const getServicio = (id) => client.get(`/api/servicios/${id}`)
export const createServicio = (body) => client.post('/api/servicios', body)
export const updateServicio = (id, body) => client.put(`/api/servicios/${id}`, body)
export const updateServicioEstado = (id, activo) => client.patch(`/api/servicios/${id}/estado`, null, { params: { activo } })
export const asignarServicio = (body) => client.post('/api/assignments', body)
export const getServiciosAsignados = (barberoId) => client.get(`/api/assignments/barbero/${barberoId}`)
export const desasignarServicio = (barberoId, servicioId) => client.delete(`/api/assignments/barbero/${barberoId}/servicio/${servicioId}`)

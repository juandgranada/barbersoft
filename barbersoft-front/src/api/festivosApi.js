import client from './client'

export const getFestivos = () => client.get('/api/festivos')
export const getFestivosAnio = (anio) => client.get(`/api/festivos/anio/${anio}`)
export const createFestivo = (body) => client.post('/api/festivos', body)
export const updateFestivo = (id, body) => client.put(`/api/festivos/${id}`, body)
export const updateFestivoEstado = (id, activo) => client.patch(`/api/festivos/${id}/estado`, null, { params: { activo } })

import client from './client'

export const getHorariosBarbero = (barberoId) => client.get(`/api/horario-barbero/barbero/${barberoId}`)
export const createHorario = (body) => client.post('/api/horario-barbero', body)
export const updateHorario = (id, body) => client.put(`/api/horario-barbero/${id}`, body)
export const updateHorarioEstado = (id, activo) => client.patch(`/api/horario-barbero/${id}/estado`, null, { params: { activo } })
export const getExcepciones = (barberoId) => client.get(`/api/excepciones-horario/barbero/${barberoId}`)
export const createExcepcion = (body) => client.post('/api/excepciones-horario', body)
export const updateExcepcionEstado = (id, activo) => client.patch(`/api/excepciones-horario/${id}/estado`, null, { params: { activo } })

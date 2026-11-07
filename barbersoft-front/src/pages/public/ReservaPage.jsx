import { useEffect, useState } from 'react'
import { getBarberos } from '@/api/barberosApi'

export default function ReservaPage() {
  const [barberos, setBarberos] = useState([])
  const [error, setError] = useState(null)

  useEffect(() => {
    getBarberos()
      .then((res) => setBarberos(res.data))
      .catch((err) => setError(err.message))
  }, [])

  return (
    <div>
      <h1>Reserva tu cita</h1>
      {error && <p>Error: {error}</p>}
      <pre>{JSON.stringify(barberos, null, 2)}</pre>
    </div>
  )
}
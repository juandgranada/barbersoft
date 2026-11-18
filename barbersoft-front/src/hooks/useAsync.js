import { useCallback, useState } from 'react'

export default function useAsync(initialValue = null) {
  const [data, setData] = useState(initialValue)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const execute = useCallback(async (task) => {
    setLoading(true)
    setError('')
    try {
      const response = await task()
      setData(response.data)
      return response.data
    } catch (errorValue) {
      setError(errorValue)
      throw errorValue
    } finally {
      setLoading(false)
    }
  }, [])

  return { data, setData, loading, error, execute }
}

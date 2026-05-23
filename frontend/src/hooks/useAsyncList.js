import { useCallback, useEffect, useState } from 'react'

export function useAsyncList(loader) {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const reload = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const data = await loader()
      setItems(data ?? [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [loader])

  useEffect(() => {
    reload()
  }, [reload])

  return {
    items,
    setItems,
    loading,
    error,
    setError,
    reload
  }
}

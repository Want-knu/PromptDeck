import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { getAccessToken, refreshAccessToken } from '../api/client'

export default function PrivateRoute({ children }) {
  const [authorized, setAuthorized] = useState(Boolean(getAccessToken()))
  const [checking, setChecking] = useState(!getAccessToken())

  useEffect(() => {
    if (getAccessToken()) {
      setAuthorized(true)
      setChecking(false)
      return
    }

    let ignore = false

    refreshAccessToken()
      .then(refreshed => {
        if (!ignore) {
          setAuthorized(refreshed)
        }
      })
      .finally(() => {
        if (!ignore) {
          setChecking(false)
        }
      })

    return () => {
      ignore = true
    }
  }, [])

  if (checking) {
    return null
  }

  return authorized ? children : <Navigate to="/login" replace />
}

import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '../api/auth'
import { getAccessToken, refreshAccessToken, setAccessToken, setUserEmail } from '../api/client'
import AuthShell from '../components/auth/AuthShell'
import { Button } from '../components/ui'
import { authStyles } from '../styles/pageStyles/authPageStyles'

export default function LoginPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (getAccessToken()) {
      navigate('/', { replace: true })
      return
    }

    let ignore = false

    refreshAccessToken().then(refreshed => {
      if (refreshed && !ignore) {
        navigate('/', { replace: true })
      }
    })

    return () => {
      ignore = true
    }
  }, [navigate])

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await login(email, password)
      setAccessToken(res.accessToken)
      setUserEmail(email)
      navigate('/')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthShell
      subtitle="LLM API 요청 빌더"
      footerText="계정이 없으신가요?"
      footerTo="/signup"
      footerLabel="회원가입"
    >
      <form onSubmit={handleSubmit} style={authStyles.form}>
        <label style={authStyles.label}>이메일</label>
        <input
          style={authStyles.input}
          type="email"
          placeholder="example@email.com"
          value={email}
          onChange={e => setEmail(e.target.value)}
          required
        />
        <label style={authStyles.label}>비밀번호</label>
        <input
          style={authStyles.input}
          type="password"
          placeholder="비밀번호 입력"
          value={password}
          onChange={e => setPassword(e.target.value)}
          required
        />
        {error && <p style={authStyles.error}>{error}</p>}
        <Button type="submit" loading={loading} style={{ marginTop: '8px', padding: '12px' }}>
          로그인
        </Button>
      </form>
    </AuthShell>
  )
}

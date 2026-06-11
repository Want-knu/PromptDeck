import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { signup } from '../api/auth'
import AuthShell from '../components/auth/AuthShell'
import { Button } from '../components/ui'
import { authStyles } from '../styles/pageStyles/authPageStyles'

export default function SignupPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '', name: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  function handleChange(e) {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await signup(form.email, form.password, form.name)
      navigate('/login')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthShell
      subtitle="새 계정 만들기"
      footerText="이미 계정이 있으신가요?"
      footerTo="/login"
      footerLabel="로그인"
    >
      <form onSubmit={handleSubmit} style={authStyles.form}>
        <label style={authStyles.label}>이름</label>
        <input
          style={authStyles.input}
          type="text"
          name="name"
          placeholder="홍길동"
          value={form.name}
          onChange={handleChange}
          required
        />
        <label style={authStyles.label}>이메일</label>
        <input
          style={authStyles.input}
          type="email"
          name="email"
          placeholder="example@email.com"
          value={form.email}
          onChange={handleChange}
          required
        />
        <label style={authStyles.label}>비밀번호</label>
        <input
          style={authStyles.input}
          type="password"
          name="password"
          placeholder="영문+숫자+특수문자 8자 이상"
          value={form.password}
          onChange={handleChange}
          required
        />
        {error && <p style={authStyles.error}>{error}</p>}
        <Button type="submit" loading={loading} style={{ marginTop: '8px', padding: '12px' }}>
          회원가입
        </Button>
      </form>
    </AuthShell>
  )
}

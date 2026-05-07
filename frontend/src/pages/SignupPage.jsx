import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { signup } from '../api/auth'

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
    <div style={styles.page}>
      <div style={styles.card}>
        <h1 style={styles.title}>PromptDeck</h1>
        <p style={styles.subtitle}>새 계정 만들기</p>
        <form onSubmit={handleSubmit} style={styles.form}>
          <label style={styles.label}>이름</label>
          <input
            style={styles.input}
            type="text"
            name="name"
            placeholder="홍길동"
            value={form.name}
            onChange={handleChange}
            required
          />
          <label style={styles.label}>이메일</label>
          <input
            style={styles.input}
            type="email"
            name="email"
            placeholder="example@email.com"
            value={form.email}
            onChange={handleChange}
            required
          />
          <label style={styles.label}>비밀번호</label>
          <input
            style={styles.input}
            type="password"
            name="password"
            placeholder="영문+숫자+특수문자 8자 이상"
            value={form.password}
            onChange={handleChange}
            required
          />
          {error && <p style={styles.error}>{error}</p>}
          <button style={styles.button} type="submit" disabled={loading}>
            {loading ? '가입 중...' : '회원가입'}
          </button>
        </form>
        <p style={styles.footer}>
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </div>
    </div>
  )
}

const styles = {
  page: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  },
  card: {
    background: '#fff',
    padding: '40px',
    borderRadius: '12px',
    boxShadow: '0 2px 16px rgba(0,0,0,0.08)',
    width: '380px'
  },
  title: { fontSize: '26px', fontWeight: 700, color: '#4f46e5', marginBottom: '4px' },
  subtitle: { fontSize: '14px', color: '#6b7280', marginBottom: '28px' },
  form: { display: 'flex', flexDirection: 'column', gap: '8px' },
  label: { fontSize: '13px', fontWeight: 600, color: '#374151' },
  input: {
    padding: '10px 14px',
    border: '1px solid #d1d5db',
    borderRadius: '8px',
    fontSize: '14px',
    outline: 'none',
    marginBottom: '8px'
  },
  button: {
    marginTop: '8px',
    padding: '12px',
    background: '#4f46e5',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    fontSize: '15px',
    fontWeight: 600,
    cursor: 'pointer'
  },
  error: { color: '#ef4444', fontSize: '13px' },
  footer: { marginTop: '20px', textAlign: 'center', fontSize: '14px', color: '#6b7280' }
}

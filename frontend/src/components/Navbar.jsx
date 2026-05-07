import { Link, useNavigate } from 'react-router-dom'
import { logout } from '../api/auth'

export default function Navbar() {
  const navigate = useNavigate()

  async function handleLogout() {
    await logout()
    navigate('/login')
  }

  return (
    <nav style={styles.nav}>
      <Link to="/" style={styles.brand}>PromptDeck</Link>
      <div style={styles.links}>
        <Link to="/" style={styles.link}>대시보드</Link>
        <Link to="/providers" style={styles.link}>Provider Keys</Link>
        <button style={styles.logoutBtn} onClick={handleLogout}>로그아웃</button>
      </div>
    </nav>
  )
}

const styles = {
  nav: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '0 24px',
    height: '56px',
    background: '#fff',
    borderBottom: '1px solid #e5e7eb',
    position: 'sticky',
    top: 0,
    zIndex: 100
  },
  brand: {
    fontSize: '18px',
    fontWeight: 700,
    color: '#4f46e5',
    textDecoration: 'none'
  },
  links: { display: 'flex', alignItems: 'center', gap: '20px' },
  link: { fontSize: '14px', color: '#374151', textDecoration: 'none' },
  logoutBtn: {
    padding: '6px 14px',
    background: 'transparent',
    border: '1px solid #d1d5db',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '14px',
    color: '#374151'
  }
}

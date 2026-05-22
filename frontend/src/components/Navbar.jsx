import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { logout } from '../api/auth'
import { getAccessToken, subscribeAuth } from '../api/client'

export default function Navbar() {
  const navigate = useNavigate()
  const [isLoggedIn, setIsLoggedIn] = useState(Boolean(getAccessToken()))

  useEffect(() => {
    return subscribeAuth(setIsLoggedIn)
  }, [])

  async function handleLogout() {
    await logout()
    navigate('/login')
  }

  return (
    <nav style={styles.nav}>
      <Link to="/" style={styles.brand}>PromptDeck</Link>
      <div style={styles.links}>
        {isLoggedIn && (
          <>
            <Link to="/" style={styles.link}>대시보드</Link>
            <Link to="/providers" style={styles.link}>Provider Keys</Link>
            <Link to="/provider-settings" style={styles.link}>Provider 설정</Link>
            <Link to="/execution" style={styles.link}>요청 실행</Link>
            <Link to="/history" style={styles.link}>기록</Link>
            <Link to="/organizations" style={styles.link}>조직</Link>
            <button style={styles.logoutBtn} onClick={handleLogout}>로그아웃</button>
          </>
        )}
        {!isLoggedIn && (
          <Link to="/login" style={styles.link}>로그인</Link>
        )}
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

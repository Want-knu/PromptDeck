import { useEffect, useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { logout } from '../api/auth'
import { getAccessToken, getUserEmail, subscribeAuth } from '../api/client'

export default function Navbar() {
  const navigate = useNavigate()
  const [isLoggedIn, setIsLoggedIn] = useState(Boolean(getAccessToken()))
  const [theme, setTheme] = useState(() => document.documentElement.dataset.theme || 'light')

  useEffect(() => {
    return subscribeAuth(setIsLoggedIn)
  }, [])

  function toggleTheme() {
    const nextTheme = theme === 'dark' ? 'light' : 'dark'
    document.documentElement.dataset.theme = nextTheme
    localStorage.setItem('pd-theme', nextTheme)
    setTheme(nextTheme)
  }

  async function handleLogout() {
    await logout()
    navigate('/login')
  }

  return (
    <nav className="pd-top-nav">
      <div className="pd-nav-inner">
        <Link to="/" className="pd-nav-brand">
          <span className="pd-brand-mark pd-nav-brand__mark">P</span>
          <span className="pd-nav-brand__text">PromptDeck</span>
        </Link>

        {isLoggedIn ? (
          <div className="pd-primary-nav" aria-label="주요 메뉴">
            <NavLink to="/" end className={linkClass}>대시보드</NavLink>
            <NavLink to="/providers" className={linkClass}>API Keys</NavLink>
            <NavLink to="/provider-settings" className={linkClass}>Provider 프로필</NavLink>
            <NavLink to="/execution" className={linkClass}>실행</NavLink>
            <NavLink to="/history" className={linkClass}>기록</NavLink>
            <NavLink to="/organizations" className={linkClass}>조직</NavLink>
          </div>
        ) : (
          <div />
        )}

        <div className="pd-nav-actions">
          {isLoggedIn ? (
            <>
              <button
                className="pd-theme-toggle"
                onClick={toggleTheme}
                aria-label={theme === 'dark' ? '라이트 모드로 전환' : '다크 모드로 전환'}
                aria-pressed={theme === 'dark'}
              >
                <span className="pd-theme-toggle__label">{theme === 'dark' ? 'Dark' : 'Light'}</span>
                <span className="pd-theme-toggle__track">
                  <span
                    className={`pd-theme-toggle__thumb ${theme === 'dark' ? 'pd-theme-toggle__thumb--dark' : ''}`}
                  />
                </span>
              </button>
              {getUserEmail() && (
                <span className="pd-nav-user">{getUserEmail()}</span>
              )}
              <button className="pd-nav-logout" onClick={handleLogout}>로그아웃</button>
            </>
          ) : (
            <Link to="/login" className="pd-nav-login">로그인</Link>
          )}
        </div>
      </div>
    </nav>
  )
}

function linkClass({ isActive }) {
  return `pd-primary-nav__link ${isActive ? 'pd-primary-nav__link--active' : ''}`
}

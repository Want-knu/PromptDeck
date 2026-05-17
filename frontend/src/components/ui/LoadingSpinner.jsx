export default function LoadingSpinner({ message = '불러오는 중...' }) {
  return (
    <div style={s.wrapper}>
      <div style={s.spinner} />
      <p style={s.message}>{message}</p>
    </div>
  )
}

const spinKeyframes = `
@keyframes pd-spin {
  to { transform: rotate(360deg); }
}
`

// inject keyframes once
if (typeof document !== 'undefined' && !document.getElementById('pd-spin-style')) {
  const style = document.createElement('style')
  style.id = 'pd-spin-style'
  style.textContent = spinKeyframes
  document.head.appendChild(style)
}

const s = {
  wrapper: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '48px 24px',
    gap: '16px'
  },
  spinner: {
    width: '32px',
    height: '32px',
    border: '3px solid #e5e7eb',
    borderTopColor: '#4f46e5',
    borderRadius: '50%',
    animation: 'pd-spin 0.7s linear infinite'
  },
  message: {
    fontSize: '14px',
    color: '#9ca3af',
    margin: 0
  }
}

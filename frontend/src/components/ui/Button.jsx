const VARIANT_STYLE = {
  primary: { background: '#4f46e5', color: '#fff', border: 'none' },
  danger: { background: '#ef4444', color: '#fff', border: 'none' },
  outline: { background: 'transparent', color: '#4f46e5', border: '1px solid #4f46e5' },
  ghost: { background: 'transparent', color: '#6b7280', border: 'none' },
  success: { background: '#16a34a', color: '#fff', border: 'none' }
}

export default function Button({ children, variant = 'primary', onClick, disabled, loading, type = 'button', style, size = 'md' }) {
  const sizeStyle = size === 'sm' ? s.sm : size === 'lg' ? s.lg : {}

  return (
    <button
      type={type}
      style={{
        ...s.base,
        ...VARIANT_STYLE[variant],
        ...sizeStyle,
        ...(disabled || loading ? s.disabled : {}),
        ...style
      }}
      onClick={onClick}
      disabled={disabled || loading}
    >
      {loading && <span style={s.spinner} />}
      {children}
    </button>
  )
}

const spinKeyframes = `
@keyframes pd-btn-spin {
  to { transform: rotate(360deg); }
}
`

if (typeof document !== 'undefined' && !document.getElementById('pd-btn-spin-style')) {
  const style = document.createElement('style')
  style.id = 'pd-btn-spin-style'
  style.textContent = spinKeyframes
  document.head.appendChild(style)
}

const s = {
  base: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '6px',
    padding: '8px 18px',
    borderRadius: '8px',
    cursor: 'pointer',
    fontWeight: 600,
    fontSize: '14px',
    whiteSpace: 'nowrap',
    transition: 'opacity 0.15s'
  },
  sm: { padding: '5px 12px', fontSize: '13px', borderRadius: '6px' },
  lg: { padding: '12px 24px', fontSize: '16px', borderRadius: '10px' },
  disabled: { opacity: 0.5, cursor: 'not-allowed' },
  spinner: {
    display: 'inline-block',
    width: '14px',
    height: '14px',
    border: '2px solid rgba(255,255,255,0.3)',
    borderTopColor: '#fff',
    borderRadius: '50%',
    animation: 'pd-btn-spin 0.6s linear infinite'
  }
}

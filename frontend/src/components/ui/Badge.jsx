const VARIANT_STYLE = {
  success: { background: '#dcfce7', color: '#16a34a' },
  error: { background: '#fee2e2', color: '#dc2626' },
  warning: { background: '#fef3c7', color: '#d97706' },
  default: { background: '#f3f4f6', color: '#6b7280' },
  info: { background: '#dbeafe', color: '#2563eb' }
}

export default function Badge({ children, variant = 'default', style }) {
  return (
    <span style={{ ...s.badge, ...VARIANT_STYLE[variant], ...style }}>
      {children}
    </span>
  )
}

const s = {
  badge: {
    display: 'inline-block',
    padding: '2px 10px',
    borderRadius: '999px',
    fontSize: '12px',
    fontWeight: 600,
    whiteSpace: 'nowrap'
  }
}

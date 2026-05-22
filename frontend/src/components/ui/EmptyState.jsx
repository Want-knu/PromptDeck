export default function EmptyState({ message = '데이터가 없습니다.', icon }) {
  return (
    <div style={s.wrapper}>
      {icon && <div style={s.icon}>{icon}</div>}
      <p style={s.message}>{message}</p>
    </div>
  )
}

const s = {
  wrapper: {
    textAlign: 'center',
    padding: '48px 24px',
    color: '#9ca3af'
  },
  icon: {
    fontSize: '40px',
    marginBottom: '12px'
  },
  message: {
    fontSize: '14px',
    margin: 0
  }
}

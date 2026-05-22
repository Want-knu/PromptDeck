export default function Card({ children, onClick, hoverable = false, style }) {
  return (
    <div
      style={{
        ...s.card,
        ...(hoverable ? s.hoverable : {}),
        ...(onClick ? s.clickable : {}),
        ...style
      }}
      onClick={onClick}
      role={onClick ? 'button' : undefined}
      tabIndex={onClick ? 0 : undefined}
      onKeyDown={onClick ? (e) => { if (e.key === 'Enter') onClick(e) } : undefined}
    >
      {children}
    </div>
  )
}

const s = {
  card: {
    background: '#fff',
    borderRadius: '12px',
    border: '1px solid #e5e7eb',
    padding: '20px'
  },
  hoverable: {
    transition: 'box-shadow 0.15s, border-color 0.15s'
  },
  clickable: {
    cursor: 'pointer'
  }
}

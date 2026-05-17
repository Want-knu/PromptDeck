export default function PageHeader({ title, actionLabel, onAction, actionDisabled }) {
  return (
    <div style={s.header}>
      <h2 style={s.heading}>{title}</h2>
      {actionLabel && (
        <button style={s.actionBtn} onClick={onAction} disabled={actionDisabled}>
          {actionLabel}
        </button>
      )}
    </div>
  )
}

const s = {
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '24px',
    flexWrap: 'wrap',
    gap: '12px'
  },
  heading: { fontSize: '22px', fontWeight: 700, margin: 0 },
  actionBtn: {
    padding: '8px 18px',
    background: '#4f46e5',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontWeight: 600,
    fontSize: '14px',
    whiteSpace: 'nowrap'
  }
}

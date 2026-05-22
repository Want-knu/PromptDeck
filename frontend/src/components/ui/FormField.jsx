export default function FormField({ label, hint, children }) {
  return (
    <div style={s.field}>
      <div style={s.labelRow}>
        <label style={s.label}>{label}</label>
        {hint && <span style={s.hint}>{hint}</span>}
      </div>
      {children}
    </div>
  )
}

const s = {
  field: { display: 'flex', flexDirection: 'column', gap: '4px' },
  labelRow: { display: 'flex', alignItems: 'center', gap: '8px' },
  label: { fontSize: '13px', fontWeight: 600, color: '#374151' },
  hint: { fontSize: '11px', color: '#9ca3af' }
}

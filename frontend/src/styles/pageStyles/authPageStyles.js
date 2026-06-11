export const authStyles = {
  form: { display: 'flex', flexDirection: 'column', gap: '8px' },
  label: { fontSize: '13px', fontWeight: 600, color: 'var(--pd-text)' },
  input: {
    padding: '10px 14px',
    border: '1px solid var(--pd-border)',
    borderRadius: '8px',
    fontSize: '14px',
    outline: 'none',
    marginBottom: '8px'
  },
  error: { color: 'var(--pd-danger)', fontSize: '13px' }
}

export const authShellStyles = {
  page: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '32px'
  },
  card: {
    background: 'var(--pd-surface-raised)',
    padding: '40px',
    borderRadius: '8px',
    border: '1px solid var(--pd-border-soft)',
    boxShadow: 'var(--pd-shadow-lg)',
    width: '400px',
    backdropFilter: 'blur(18px)'
  },
  brandMark: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    width: '44px',
    height: '44px',
    borderRadius: '8px',
    marginBottom: '18px',
    background: 'var(--pd-primary-gradient)',
    color: 'var(--pd-on-primary)',
    boxShadow: 'var(--pd-glow)',
    fontWeight: 900,
    fontSize: '20px'
  },
  title: { fontSize: '30px', fontWeight: 900, color: 'var(--pd-heading)', marginBottom: '4px', letterSpacing: 0 },
  subtitle: { fontSize: '14px', color: 'var(--pd-muted)', marginBottom: '28px' },
  footer: { marginTop: '20px', textAlign: 'center', fontSize: '14px', color: 'var(--pd-muted)' }
}

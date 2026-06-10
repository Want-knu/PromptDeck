export const dashboardPageStyles = {
  main: { maxWidth: '1120px', margin: '36px auto', padding: '0 24px 56px' },
  hero: {
    display: 'flex',
    justifyContent: 'space-between',
    gap: '24px',
    alignItems: 'flex-end',
    padding: '34px',
    marginBottom: '28px',
    borderRadius: '8px',
    border: '1px solid var(--pd-border-soft)',
    background: 'var(--pd-primary-gradient)',
    color: 'var(--pd-on-primary)',
    boxShadow: 'var(--pd-glow)',
    overflow: 'hidden'
  },
  eyebrow: { fontSize: '12px', fontWeight: 800, textTransform: 'uppercase', marginBottom: '10px', opacity: 0.82 },
  heroTitle: { fontSize: '46px', fontWeight: 900, marginBottom: '8px', letterSpacing: 0 },
  heroDesc: { maxWidth: '640px', fontSize: '15px', lineHeight: 1.7, opacity: 0.9 },
  heroAction: {
    flex: '0 0 auto',
    padding: '12px 18px',
    borderRadius: '8px',
    background: 'rgba(255,255,255,0.18)',
    color: '#fff',
    border: '1px solid rgba(255,255,255,0.42)',
    textDecoration: 'none',
    fontWeight: 800,
    backdropFilter: 'blur(12px)'
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
    gap: '20px'
  },
  card: {
    minHeight: '210px',
    background: 'var(--pd-surface-raised)',
    borderRadius: '8px',
    padding: '24px',
    boxShadow: 'var(--pd-shadow-sm)',
    display: 'flex',
    flexDirection: 'column'
  },
  cardTop: { display: 'flex', justifyContent: 'space-between', marginBottom: '22px' },
  cardStat: { padding: '5px 10px', borderRadius: '8px', background: 'var(--pd-accent-soft)', color: 'var(--pd-accent)', fontSize: '12px', fontWeight: 800 },
  cardTitle: { fontSize: '18px', fontWeight: 800, marginBottom: '10px', color: 'var(--pd-heading)' },
  cardDesc: { fontSize: '13px', color: 'var(--pd-muted)', lineHeight: 1.7, marginBottom: '18px', flex: 1 },
  cardLink: {
    display: 'inline-block',
    alignSelf: 'flex-start',
    padding: '9px 16px',
    background: 'var(--pd-secondary-gradient)',
    color: 'var(--pd-on-primary)',
    borderRadius: '8px',
    fontSize: '13px',
    fontWeight: 800,
    textDecoration: 'none',
    boxShadow: 'var(--pd-shadow-sm)'
  },
  cardLinkDisabled: {
    background: 'var(--pd-border-soft)',
    color: 'var(--pd-muted-soft)',
    cursor: 'not-allowed'
  }
}

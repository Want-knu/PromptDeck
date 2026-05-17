export default function Tabs({ tabs, activeTab, onChange }) {
  return (
    <div style={s.wrapper}>
      {tabs.map(tab => (
        <button
          key={tab.key}
          style={{
            ...s.tab,
            ...(activeTab === tab.key ? s.activeTab : {})
          }}
          onClick={() => onChange(tab.key)}
        >
          {tab.label}
        </button>
      ))}
    </div>
  )
}

const s = {
  wrapper: {
    display: 'flex',
    gap: '4px',
    background: '#f3f4f6',
    padding: '4px',
    borderRadius: '10px',
    marginBottom: '20px'
  },
  tab: {
    flex: 1,
    padding: '8px 16px',
    border: 'none',
    borderRadius: '8px',
    background: 'transparent',
    color: '#6b7280',
    fontSize: '13px',
    fontWeight: 600,
    cursor: 'pointer',
    transition: 'background 0.15s, color 0.15s'
  },
  activeTab: {
    background: '#fff',
    color: '#111827',
    boxShadow: '0 1px 3px rgba(0,0,0,0.08)'
  }
}

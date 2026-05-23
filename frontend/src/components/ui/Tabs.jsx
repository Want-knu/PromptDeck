export default function Tabs({ tabs, activeTab, onChange }) {
  return (
    <div className="pd-tabs">
      {tabs.map(tab => (
        <button
          key={tab.key}
          className={`pd-tabs__tab ${activeTab === tab.key ? 'pd-tabs__tab--active' : ''}`}
          onClick={() => onChange(tab.key)}
        >
          {tab.label}
        </button>
      ))}
    </div>
  )
}

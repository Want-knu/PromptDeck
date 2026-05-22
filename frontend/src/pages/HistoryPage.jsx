import { useState, useEffect } from 'react'
import Navbar from '../components/Navbar'
import { PageHeader, Card, Button, Badge, LoadingSpinner, EmptyState } from '../components/ui'
import { getExecutionHistory } from '../api/executions'
import { getOrganizations } from '../api/organizations'

export default function HistoryPage() {
  const [history, setHistory] = useState([])
  const [organizations, setOrganizations] = useState([])
  const [organizationId, setOrganizationId] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [expanded, setExpanded] = useState(null)

  useEffect(() => {
    getOrganizations()
      .then(data => setOrganizations(data ?? []))
      .catch(() => setOrganizations([]))
  }, [])

  useEffect(() => { fetchHistory() }, [organizationId])

  async function fetchHistory() {
    setLoading(true)
    setError('')
    try {
      const data = await getExecutionHistory(organizationId || undefined)
      setHistory(data ?? [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  function toggleExpand(id) {
    setExpanded(prev => (prev === id ? null : id))
  }

  function formatDate(str) {
    if (!str) return '-'
    const d = new Date(str)
    return d.toLocaleString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' })
  }

  function tryFormatJson(str) {
    try { return JSON.stringify(JSON.parse(str), null, 2) } catch { return str }
  }

  return (
    <>
      <Navbar />
      <main style={s.main}>
        <PageHeader title="요청 기록" actionLabel="새로고침" onAction={fetchHistory} />

        <div style={s.filterRow}>
          <label style={s.filterLabel} htmlFor="history-organization">조직</label>
          <select
            id="history-organization"
            style={s.select}
            value={organizationId}
            onChange={e => setOrganizationId(e.target.value)}
          >
            <option value="">내 기록</option>
            {organizations.map(org => (
              <option key={org.id} value={org.id}>{org.name}</option>
            ))}
          </select>
        </div>

        {loading && <LoadingSpinner />}
        {error && <p style={s.error}>{error}</p>}
        {!loading && history.length === 0 && (
          <EmptyState message="실행 기록이 없습니다. 요청 실행 페이지에서 먼저 실행해보세요." />
        )}

        <div style={s.list}>
          {history.map(h => {
            const isExpanded = expanded === h.id
            const badgeVariant = h.success == null ? 'default' : h.success ? 'success' : 'error'
            const badgeText = h.success == null ? `- ${h.statusCode ?? ''}` : `${h.success ? '성공' : '실패'} ${h.statusCode ?? ''}`

            return (
              <Card key={h.id} style={s.card}>
                <div style={s.row} onClick={() => toggleExpand(h.id)}>
                  <div style={s.rowLeft}>
                    <Badge variant={badgeVariant}>{badgeText}</Badge>
                    <div>
                      <p style={s.cardTitle}>{h.providerType} · {h.model}</p>
                      <p style={s.cardSub}>{formatDate(h.createdAt)} · {h.durationMs != null ? `${h.durationMs}ms` : '-'}</p>
                    </div>
                  </div>
                  <span style={s.toggle}>{isExpanded ? '▲' : '▼'}</span>
                </div>

                {isExpanded && (
                  <div style={s.detail}>
                    {h.parsedResponse && (
                      <>
                        <p style={s.detailLabel}>파싱된 응답</p>
                        <div style={s.parsedBox}>{h.parsedResponse}</div>
                      </>
                    )}

                    {h.errorMessage && (
                      <>
                        <p style={s.detailLabel}>에러 메시지</p>
                        <p style={s.error}>{h.errorMessage}</p>
                      </>
                    )}

                    {h.requestJson && (
                      <>
                        <p style={s.detailLabel}>요청 JSON</p>
                        <pre style={s.pre}>{tryFormatJson(h.requestJson)}</pre>
                      </>
                    )}

                    {h.responseBody && (
                      <>
                        <p style={s.detailLabel}>원본 응답</p>
                        <pre style={{ ...s.pre, maxHeight: '280px', overflow: 'auto' }}>{tryFormatJson(h.responseBody)}</pre>
                      </>
                    )}

                    <div style={s.metaRow}>
                      <span style={s.metaItem}>Setting ID: {h.providerSettingId}</span>
                      <span style={s.metaItem}>Key ID: {h.providerKeyId}</span>
                      {h.organizationId && <span style={s.metaItem}>Org ID: {h.organizationId}</span>}
                    </div>
                  </div>
                )}
              </Card>
            )
          })}
        </div>
      </main>
    </>
  )
}

const s = {
  main: { maxWidth: '860px', margin: '40px auto', padding: '0 24px' },
  filterRow: { display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '18px' },
  filterLabel: { fontSize: '13px', fontWeight: 600, color: '#374151' },
  select: { padding: '8px 12px', border: '1px solid #d1d5db', borderRadius: '6px', fontSize: '14px', outline: 'none' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
  heading: { fontSize: '22px', fontWeight: 700 },
  refreshBtn: { padding: '8px 16px', background: 'transparent', border: '1px solid #d1d5db', borderRadius: '8px', cursor: 'pointer', fontWeight: 500 },
  info: { color: '#6b7280', fontSize: '14px' },
  error: { color: '#ef4444', fontSize: '13px' },
  list: { display: 'flex', flexDirection: 'column', gap: '10px' },
  card: { background: '#fff', borderRadius: '10px', boxShadow: '0 1px 6px rgba(0,0,0,0.06)', overflow: 'hidden' },
  row: { padding: '16px 20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer', userSelect: 'none' },
  rowLeft: { display: 'flex', alignItems: 'center', gap: '14px' },
  badge: { padding: '3px 10px', borderRadius: '20px', fontSize: '12px', fontWeight: 700, whiteSpace: 'nowrap' },
  cardTitle: { fontSize: '14px', fontWeight: 700, marginBottom: '2px' },
  cardSub: { fontSize: '12px', color: '#6b7280' },
  toggle: { fontSize: '11px', color: '#9ca3af' },
  detail: { borderTop: '1px solid #f3f4f6', padding: '16px 20px', display: 'flex', flexDirection: 'column', gap: '6px' },
  detailLabel: { fontSize: '12px', fontWeight: 600, color: '#6b7280', marginTop: '8px', marginBottom: '4px' },
  parsedBox: { background: '#f0fdf4', border: '1px solid #86efac', borderRadius: '8px', padding: '12px', fontSize: '14px', lineHeight: 1.7, whiteSpace: 'pre-wrap', wordBreak: 'break-word' },
  pre: { background: '#1e293b', color: '#e2e8f0', borderRadius: '8px', padding: '12px', fontSize: '12px', fontFamily: 'monospace', whiteSpace: 'pre-wrap', wordBreak: 'break-all' },
  metaRow: { display: 'flex', gap: '16px', marginTop: '8px', flexWrap: 'wrap' },
  metaItem: { fontSize: '11px', color: '#9ca3af' }
}

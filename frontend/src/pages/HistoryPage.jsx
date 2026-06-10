import { useState, useEffect } from 'react'
import Navbar from '../components/Navbar'
import { PageHeader, Card, Button, Badge, LoadingSpinner, EmptyState } from '../components/ui'
import { getExecutionHistory } from '../api/executions'
import { getOrganizations } from '../api/organizations'
import { historyPageStyles as s } from '../styles/pageStyles/historyPageStyles'

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
      <main className="pd-page-enter" style={s.main}>
        <PageHeader
          title="요청 기록"
          eyebrow="Observability"
          description="실행한 요청의 결과, 원본 응답, 소요 시간과 오류를 다시 확인합니다."
          actionLabel="새로고침"
          onAction={fetchHistory}
        />

        <div className="pd-card pd-stagger-1" style={s.filterRow}>
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
          <EmptyState message="실행 기록이 없습니다. 실행 페이지에서 먼저 요청을 실행해보세요." />
        )}

        <div style={s.list}>
          {history.map(h => {
            const isExpanded = expanded === h.id
            const badgeVariant = h.success == null ? 'default' : h.success ? 'success' : 'error'
            const badgeText = h.success == null ? `- ${h.statusCode ?? ''}` : `${h.success ? '성공' : '실패'} ${h.statusCode ?? ''}`

            return (
              <Card key={h.id} className="pd-stagger-1" style={s.card}>
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
                <div className="pd-result-enter" style={s.detail}>
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

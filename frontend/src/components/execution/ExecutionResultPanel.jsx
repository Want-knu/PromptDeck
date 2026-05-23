import { Badge, Card } from '../ui'
import { formatJsonString } from '../../utils/executionOptions'

export default function ExecutionResultPanel({ preview, result, styles }) {
  return (
    <Card className="pd-stagger-2" style={styles.panel}>
      <div style={styles.panelHeader}>
        <span style={styles.panelKicker}>Response</span>
        <h3 style={styles.subheading}>결과</h3>
      </div>

      {!preview && !result && (
        <p style={styles.info}>미리보기 또는 실행 결과가 여기에 표시됩니다.</p>
      )}

      {preview && <PreviewResult preview={preview} styles={styles} />}
      {result && <ExecutionResult result={result} styles={styles} />}
    </Card>
  )
}

function PreviewResult({ preview, styles }) {
  return (
    <div className="pd-result-enter">
      <p style={styles.resultLabel}>요청 미리보기</p>
      <div style={styles.resultMeta}>
        <Badge variant="info">{preview.method}</Badge>
        <code style={styles.endpoint}>{preview.endpoint}</code>
      </div>
      {preview.headers && Object.keys(preview.headers).length > 0 && (
        <>
          <p style={styles.sectionLabel}>헤더</p>
          <pre style={styles.pre}>{JSON.stringify(preview.headers, null, 2)}</pre>
        </>
      )}
      {preview.body && (
        <>
          <p style={styles.sectionLabel}>바디</p>
          <pre style={styles.pre}>{JSON.stringify(preview.body, null, 2)}</pre>
        </>
      )}
    </div>
  )
}

function ExecutionResult({ result, styles }) {
  return (
    <div className="pd-result-enter">
      <div style={styles.resultHeader}>
        <p style={styles.resultLabel}>실행 결과</p>
        <Badge variant={result.success ? 'success' : 'error'}>
          {result.statusCode} {result.success ? '성공' : '실패'}
        </Badge>
      </div>
      <p style={styles.sectionLabel}>{result.providerType} · {result.model}</p>

      {result.parsedResponse && (
        <>
          <p style={styles.sectionLabel}>파싱된 응답</p>
          <div style={styles.parsedBox}>{result.parsedResponse}</div>
        </>
      )}

      {result.errorMessage && (
        <>
          <p style={styles.sectionLabel}>에러 메시지</p>
          <p style={styles.error}>{result.errorMessage}</p>
        </>
      )}

      <p style={styles.sectionLabel}>원본 응답</p>
      <pre style={{ ...styles.pre, maxHeight: '300px', overflow: 'auto' }}>
        {formatJsonString(result.responseBody)}
      </pre>
    </div>
  )
}

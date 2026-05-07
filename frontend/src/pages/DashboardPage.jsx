import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar'

const FEATURES = [
  {
    title: 'Provider Key 관리',
    description: 'OpenAI, Gemini, Claude 등 LLM Provider의 API Key를 안전하게 등록하고 관리합니다.',
    link: '/providers',
    label: '관리하기'
  },
  {
    title: '프롬프트 프리셋',
    description: '자주 사용하는 system prompt와 user prompt 템플릿을 프리셋으로 저장합니다.',
    link: '#',
    label: '준비 중'
  },
  {
    title: '요청 실행',
    description: 'Provider와 프리셋을 선택하고 LLM API 요청을 빌드·실행합니다.',
    link: '#',
    label: '준비 중'
  },
  {
    title: '요청 기록',
    description: '과거 요청의 Provider, 프리셋, 요청/응답 내용, 오류를 확인합니다.',
    link: '#',
    label: '준비 중'
  }
]

export default function DashboardPage() {
  return (
    <>
      <Navbar />
      <main style={styles.main}>
        <h2 style={styles.heading}>대시보드</h2>
        <p style={styles.desc}>PromptDeck에 오신 것을 환영합니다. 아래 기능을 사용해보세요.</p>
        <div style={styles.grid}>
          {FEATURES.map(f => (
            <div key={f.title} style={styles.card}>
              <h3 style={styles.cardTitle}>{f.title}</h3>
              <p style={styles.cardDesc}>{f.description}</p>
              <Link
                to={f.link}
                style={{
                  ...styles.cardLink,
                  ...(f.link === '#' ? styles.cardLinkDisabled : {})
                }}
                onClick={f.link === '#' ? e => e.preventDefault() : undefined}
              >
                {f.label}
              </Link>
            </div>
          ))}
        </div>
      </main>
    </>
  )
}

const styles = {
  main: { maxWidth: '900px', margin: '40px auto', padding: '0 24px' },
  heading: { fontSize: '22px', fontWeight: 700, marginBottom: '8px' },
  desc: { fontSize: '14px', color: '#6b7280', marginBottom: '32px' },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
    gap: '20px'
  },
  card: {
    background: '#fff',
    borderRadius: '10px',
    padding: '24px',
    boxShadow: '0 1px 8px rgba(0,0,0,0.07)'
  },
  cardTitle: { fontSize: '16px', fontWeight: 700, marginBottom: '10px' },
  cardDesc: { fontSize: '13px', color: '#6b7280', lineHeight: 1.6, marginBottom: '16px' },
  cardLink: {
    display: 'inline-block',
    padding: '7px 16px',
    background: '#4f46e5',
    color: '#fff',
    borderRadius: '6px',
    fontSize: '13px',
    fontWeight: 600,
    textDecoration: 'none'
  },
  cardLinkDisabled: {
    background: '#e5e7eb',
    color: '#9ca3af',
    cursor: 'not-allowed'
  }
}

import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { PageHeader, Card } from '../components/ui'

const FEATURES = [
  {
    title: 'Provider Key 관리',
    description: 'OpenAI, Gemini, Claude 등 LLM Provider의 API Key를 안전하게 등록하고 관리합니다.',
    link: '/providers',
    label: '관리하기'
  },
  {
    title: 'Provider 설정 관리',
    description: '엔드포인트, 인증 방식, 바디 템플릿 등 Provider 호출 설정 프리셋을 만들고 관리합니다.',
    link: '/provider-settings',
    label: '관리하기'
  },
  {
    title: '요청 실행',
    description: 'Provider Key와 설정을 선택하고 프롬프트를 입력해 LLM API 요청을 빌드·실행합니다.',
    link: '/execution',
    label: '실행하기'
  },
  {
    title: '요청 기록',
    description: '과거 요청의 Provider, 설정, 요청/응답 내용, 소요 시간, 오류를 확인합니다.',
    link: '/history',
    label: '기록 보기'
  }
]

export default function DashboardPage() {
  return (
    <>
      <Navbar />
      <main style={styles.main}>
        <PageHeader title="대시보드" />
        <p style={styles.desc}>PromptDeck에 오신 것을 환영합니다. 아래 기능을 사용해보세요.</p>
        <div style={styles.grid}>
          {FEATURES.map(f => (
            <Card key={f.title} hoverable style={styles.card}>
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
            </Card>
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

import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { PageHeader, Card } from '../components/ui'
import { dashboardPageStyles as styles } from '../styles/pageStyles/dashboardPageStyles'

const FEATURES = [
  {
    stat: 'Keys',
    title: 'API Key 관리',
    description: 'OpenAI, Gemini, Claude 등 LLM Provider의 API Key를 안전하게 등록하고 관리합니다.',
    link: '/providers',
    label: '관리하기'
  },
  {
    stat: 'Models',
    title: 'Provider 프로필',
    description: 'Provider와 모델 조합을 저장하고, CUSTOM Provider의 연결 방식을 관리합니다.',
    link: '/provider-settings',
    label: '관리하기'
  },
  {
    stat: 'Run',
    title: '실행',
    description: 'Provider 프로필과 API Key를 선택하고 프롬프트, 옵션, 프리셋을 관리합니다.',
    link: '/execution',
    label: '실행하기'
  },
  {
    stat: 'Logs',
    title: '실행 기록',
    description: '과거 요청의 Provider, 설정, 요청/응답 내용, 소요 시간, 오류를 확인합니다.',
    link: '/history',
    label: '기록 보기'
  }
]

export default function DashboardPage() {
  return (
    <>
      <Navbar />
      <main className="pd-page-enter" style={styles.main}>
        <section className="pd-hero" style={styles.hero}>
          <div>
            <p style={styles.eyebrow}>LLM workflow console</p>
            <h1 style={styles.heroTitle}>PromptDeck</h1>
            <p style={styles.heroDesc}>Provider, Key, Preset, History를 한 화면 흐름으로 연결해 빠르게 실험하고 재사용하는 실행 콘솔입니다.</p>
          </div>
          <Link to="/execution" style={styles.heroAction}>바로 실행</Link>
        </section>
        <PageHeader
          title="워크스페이스"
          eyebrow="Overview"
          description="자주 쓰는 관리 화면과 실행 흐름으로 빠르게 이동합니다."
        />
        <div style={styles.grid}>
          {FEATURES.map(f => (
            <Card key={f.title} hoverable className={`pd-stagger-${indexForFeature(f.title)}`} style={styles.card}>
              <div style={styles.cardTop}>
                <span style={styles.cardStat}>{f.stat}</span>
              </div>
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

function indexForFeature(title) {
  const index = FEATURES.findIndex(feature => feature.title === title)
  return Math.min(index + 1, 4)
}

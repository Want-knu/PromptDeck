export default function ExecutionHero({ settingsCount, presetsCount, keysCount, styles }) {
  return (
    <section className="pd-hero" style={styles.hero}>
      <div>
        <p style={styles.eyebrow}>Prompt execution</p>
        <h1 style={styles.heroTitle}>실행</h1>
        <p style={styles.heroDesc}>
          저장된 Provider 프로필과 프리셋을 선택하고, 요청 미리보기부터 실제 실행 결과까지 한 흐름에서 확인합니다.
        </p>
      </div>
      <div style={styles.heroStats}>
        <Stat value={settingsCount} label="프로필" styles={styles} />
        <Stat value={presetsCount} label="프리셋" styles={styles} />
        <Stat value={keysCount} label="API Keys" styles={styles} />
      </div>
    </section>
  )
}

function Stat({ value, label, styles }) {
  return (
    <div style={styles.heroStat}>
      <strong>{value}</strong>
      <span>{label}</span>
    </div>
  )
}

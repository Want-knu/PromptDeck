import { Link } from 'react-router-dom'
import { authShellStyles as styles } from '../../styles/pageStyles/authPageStyles'

export default function AuthShell({ subtitle, footerText, footerTo, footerLabel, children }) {
  return (
    <div className="pd-page-enter" style={styles.page}>
      <div className="pd-card" style={styles.card}>
        <div className="pd-brand-mark" style={styles.brandMark}>P</div>
        <h1 style={styles.title}>PromptDeck</h1>
        <p style={styles.subtitle}>{subtitle}</p>
        {children}
        <p style={styles.footer}>
          {footerText} <Link to={footerTo}>{footerLabel}</Link>
        </p>
      </div>
    </div>
  )
}

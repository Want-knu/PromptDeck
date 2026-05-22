import { useEffect, useRef } from 'react'

export default function Modal({ isOpen, onClose, title, children, footer, width = '640px' }) {
  const overlayRef = useRef(null)

  useEffect(() => {
    if (!isOpen) return
    const handler = (e) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', handler)
    return () => document.removeEventListener('keydown', handler)
  }, [isOpen, onClose])

  if (!isOpen) return null

  return (
    <div
      ref={overlayRef}
      style={s.overlay}
      onClick={(e) => { if (e.target === overlayRef.current) onClose() }}
    >
      <div style={{ ...s.modal, maxWidth: width }}>
        <div style={s.header}>
          <h3 style={s.title}>{title}</h3>
          <button style={s.closeBtn} onClick={onClose} aria-label="닫기">✕</button>
        </div>
        <div style={s.body}>{children}</div>
        {footer && <div style={s.footer}>{footer}</div>}
      </div>
    </div>
  )
}

const s = {
  overlay: {
    position: 'fixed',
    inset: 0,
    background: 'rgba(0,0,0,0.4)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1000,
    padding: '24px'
  },
  modal: {
    background: '#fff',
    borderRadius: '16px',
    width: '100%',
    maxHeight: '90vh',
    display: 'flex',
    flexDirection: 'column',
    boxShadow: '0 20px 60px rgba(0,0,0,0.15)'
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '20px 24px 0'
  },
  title: { fontSize: '18px', fontWeight: 700, margin: 0 },
  closeBtn: {
    background: 'none',
    border: 'none',
    fontSize: '20px',
    cursor: 'pointer',
    color: '#9ca3af',
    padding: '4px 8px',
    borderRadius: '6px'
  },
  body: {
    padding: '20px 24px',
    overflowY: 'auto',
    flex: 1
  },
  footer: {
    padding: '16px 24px',
    borderTop: '1px solid #e5e7eb',
    display: 'flex',
    justifyContent: 'flex-end',
    gap: '8px'
  }
}

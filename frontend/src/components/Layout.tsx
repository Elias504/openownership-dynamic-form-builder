import { Link } from 'react-router-dom'

interface Breadcrumb {
  label: string
  to?: string
  onClick?: () => void
}

interface LayoutProps {
  title: string
  breadcrumbs?: Breadcrumb[]
  action?: React.ReactNode
  children: React.ReactNode
}

export default function Layout({ title, breadcrumbs, action, children }: LayoutProps) {
  return (
    <div>
      <header className="site-header">
        <div className="site-header__inner container">
          <Link to="/" className="site-header__logo">Open Ownership</Link>
          <span className="site-header__sep">|</span>
          <span className="site-header__product">Form Builder</span>
        </div>
      </header>

      <div className="page-hero">
        <div className="container">
          {breadcrumbs && breadcrumbs.length > 0 && (
            <nav className="page-hero__breadcrumbs" aria-label="breadcrumb">
              {breadcrumbs.map((crumb, i) => (
                <span key={i} className="flex items-center gap-2">
                  {i > 0 && <span className="sep">›</span>}
                  {crumb.to
                    ? <Link to={crumb.to}>{crumb.label}</Link>
                    : crumb.onClick
                    ? <button onClick={crumb.onClick}>{crumb.label}</button>
                    : <span>{crumb.label}</span>}
                </span>
              ))}
            </nav>
          )}
          <div className="page-hero__header">
            <h1 className="page-hero__title">{title}</h1>
            {action && <div>{action}</div>}
          </div>
        </div>
      </div>

      <main className="page-content">
        <div className="container">
          {children}
        </div>
      </main>
    </div>
  )
}
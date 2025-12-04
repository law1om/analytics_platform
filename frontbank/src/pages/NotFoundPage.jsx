import { Link } from 'react-router-dom'

function NotFoundPage() {
  return (
    <div className="form-card" style={{ textAlign: 'center' }}>
      <div className="form-title">Страница не найдена</div>
      <div className="form-subtitle" style={{ marginBottom: 20 }}>
        Проверьте адрес или вернитесь на дашборд.
      </div>
      <Link to="/dashboard" className="app-button app-button-primary">
        На дашборд
      </Link>
    </div>
  )
}

export default NotFoundPage

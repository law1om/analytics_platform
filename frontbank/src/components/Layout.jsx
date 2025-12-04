import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function Layout() {
  const { user, isAdmin, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <aside className="app-sidebar">
        <div>
          <div className="app-logo">BANK STRATEGY ANALYTICS</div>
        </div>
        <nav className="app-nav">
          <NavLink
            to="/dashboard"
            className={({ isActive }) =>
              `app-nav-link ${isActive ? 'active' : ''}`
            }
          >
            <span>Дашборд</span>
            {location.pathname === '/dashboard' && <span>●</span>}
          </NavLink>
          
          {!isAdmin && (
            <NavLink
              to="/my-division"
              className={({ isActive }) =>
                `app-nav-link ${isActive ? 'active' : ''}`
              }
            >
              <span>Мой отдел</span>
              {location.pathname === '/my-division' && <span>●</span>}
            </NavLink>
          )}

          {isAdmin && (
            <>
              <NavLink
                to="/upload"
                className={({ isActive }) =>
                  `app-nav-link ${isActive ? 'active' : ''}`
                }
              >
                <span>Загрузка Excel</span>
                {location.pathname.startsWith('/upload') && <span>●</span>}
              </NavLink>
              <NavLink
                to="/divisions"
                className={({ isActive }) =>
                  `app-nav-link ${isActive ? 'active' : ''}`
                }
              >
                <span>Подразделения</span>
                {location.pathname.startsWith('/divisions') && <span>●</span>}
              </NavLink>
            </>
          )}
        </nav>
      </aside>
      <main className="app-main">
        <header className="app-topbar">
          <div className="app-topbar-title">
            {location.pathname === '/dashboard' && 'Общий прогресс банка'}
            {location.pathname === '/my-division' && 'Мой отдел'}
            {location.pathname.startsWith('/upload') && 'Загрузка Excel отчётов'}
            {location.pathname.startsWith('/divisions') && 'Подразделения и цели'}
          </div>
          <div className="app-topbar-user">
            {user && (
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: 13 }}>{user.fullName || user.email}</div>
                <div style={{ fontSize: 11, color: '#9ca3af' }}>
                  <span
                    className={
                      'badge-role ' + (isAdmin ? '' : 'badge-role-user')
                    }
                  >
                    {isAdmin ? 'ADMIN' : 'USER'}
                  </span>
                </div>
              </div>
            )}
            <button
              type="button"
              className="app-button app-button-ghost"
              onClick={handleLogout}
            >
              Выйти
            </button>
          </div>
        </header>
        <Outlet />
      </main>
    </div>
  )
}

export default Layout

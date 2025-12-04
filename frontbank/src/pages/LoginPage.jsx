import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { API_BASE_URL } from '../api/client'
import { useAuth } from '../context/AuthContext'

function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()
  const location = useLocation()
  const { login: saveAuth } = useAuth()

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      const basicToken = `Basic ${btoa(`${username}:${password}`)}`


      const response = await fetch(`${API_BASE_URL}/users`, {
        headers: {
          Authorization: basicToken,
        },
      })

      if (!response.ok) {
        throw new Error('Неверный логин или пароль')
      }

      const users = await response.json()
      

      const currentUser = users.find(u => u.email === username || u.name === username)
      
      if (!currentUser) {
        throw new Error('Пользователь не найден')
      }

      saveAuth({
        token: basicToken,
        email: currentUser.email,
        fullName: currentUser.name,
        role: currentUser.role || 'EMPLOYEE',
        divisionId: currentUser.divisionId,
      })
      const from = location.state?.from?.pathname || '/dashboard'
      navigate(from, { replace: true })
    } catch (e) {
      setError(e.message || 'Ошибка авторизации')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app-main">
      <div className="form-card">
        <div className="form-title">Вход в аналитическую платформу</div>
        <div className="form-subtitle">
          Используйте логин и пароль, настроенные в системе безопасности
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-field">
            <label className="form-label" htmlFor="username">
              Логин
            </label>
            <input
              id="username"
              type="text"
              className="form-input"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              autoComplete="username"
              required
            />
          </div>
          <div className="form-field">
            <label className="form-label" htmlFor="password">
              Пароль
            </label>
            <input
              id="password"
              type="password"
              className="form-input"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              autoComplete="current-password"
              required
            />
          </div>
          {error && <div className="form-error">{error}</div>}
          <button
            type="submit"
            className="app-button app-button-primary"
            style={{ width: '100%', marginTop: 10 }}
            disabled={loading}
          >
            {loading ? 'Вход...' : 'Войти'}
          </button>
        </form>
      </div>
    </div>
  )
}

export default LoginPage

import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { fetchDivisions } from '../api/client'

function DivisionsPage() {
  const [divisions, setDivisions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    let isCancelled = false
    async function load() {
      setLoading(true)
      setError('')
      try {
        const data = await fetchDivisions()
        if (!isCancelled) {
          setDivisions(data || [])
        }
      } catch (e) {
        if (!isCancelled) {
          setError(e.message || 'Ошибка загрузки подразделений')
        }
      } finally {
        if (!isCancelled) {
          setLoading(false)
        }
      }
    }
    load()
    return () => {
      isCancelled = true
    }
  }, [])

  const handleRowClick = (division) => {
    navigate(`/divisions/${division.id}/blocks`)
  }

  return (
    <div className="app-card">
      <div className="app-card-header">
        <div className="app-card-title">Все подразделения банка</div>
        <div className="app-badge">ADMIN</div>
      </div>
      <table className="app-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Подразделение</th>
            <th>Ответственные исполнители</th>
          </tr>
        </thead>
        <tbody>
          {divisions.map((division) => (
            <tr
              key={division.id}
              style={{ cursor: 'pointer' }}
              onClick={() => handleRowClick(division)}
            >
              <td>{division.id}</td>
              <td>{division.name}</td>
              <td>
                {division.blocks && division.blocks.length > 0
                  ? division.blocks.join(', ')
                  : '—'}
              </td>
            </tr>
          ))}
          {divisions.length === 0 && !loading && (
            <tr>
              <td colSpan={3} style={{ fontSize: 13, color: '#9ca3af' }}>
                Нет данных о подразделениях. Загрузите отчёты или настройте API.
              </td>
            </tr>
          )}
        </tbody>
      </table>
      {loading && (
        <div style={{ marginTop: 12, fontSize: 13, color: '#9ca3af' }}>
          Загрузка списка подразделений...
        </div>
      )}
      {error && (
        <div className="form-error" style={{ marginTop: 12 }}>
          {error}
        </div>
      )}
    </div>
  )
}

export default DivisionsPage

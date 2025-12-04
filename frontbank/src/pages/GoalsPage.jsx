import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'

function GoalsPage() {
  const { divisionId } = useParams()
  const [divisionName, setDivisionName] = useState('')
  const [goals, setGoals] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function load() {
      setLoading(true)
      setError('')
      try {
        const token = localStorage.getItem('authToken')
        const headers = { 'Authorization': token || '', 'Content-Type': 'application/json' }

        const [goalsRes, divisionRes] = await Promise.all([
          fetch(`http://localhost:8080/goals/division/${divisionId}`, { headers }),
          fetch(`http://localhost:8080/divisions/${divisionId}`, { headers })
        ])

        if (!goalsRes.ok || !divisionRes.ok) {
          throw new Error('Ошибка загрузки данных')
        }

        const goalsData = await goalsRes.json()
        const divisionData = await divisionRes.json()

        setDivisionName(divisionData.name || '')
        setGoals(goalsData || [])
      } catch (e) {
        setError(e.message || 'Ошибка загрузки целей подразделения')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [divisionId])

  return (
    <div className="app-card">
      <div className="app-card-header">
        <div className="app-card-title">
          Цели и задачи подразделения {divisionName || divisionId}
        </div>
        <div className="app-badge">ADMIN</div>
      </div>
      <table className="app-table">
        <thead>
          <tr>
            <th>Цель</th>
            <th>Срок</th>
            <th>Прогресс</th>
          </tr>
        </thead>
        <tbody>
          {goals.map((goal) => (
            <tr key={goal.id}>
              <td>{goal.title}</td>
              <td>{goal.deadline ?? '—'}</td>
              <td>{goal.progress ?? 0}%</td>
            </tr>
          ))}
          {goals.length === 0 && !loading && (
            <tr>
              <td colSpan={3} style={{ fontSize: 13, color: '#9ca3af' }}>
                Цели для этого подразделения отсутствуют.
              </td>
            </tr>
          )}
        </tbody>
      </table>
      {loading && (
        <div style={{ marginTop: 12, fontSize: 13, color: '#9ca3af' }}>
          Загрузка целей и задач...
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

export default GoalsPage

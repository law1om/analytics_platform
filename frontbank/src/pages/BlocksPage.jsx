import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'

function BlocksPage() {
  const { divisionId } = useParams()
  const navigate = useNavigate()
  const [division, setDivision] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadDivision() {
      setLoading(true)
      setError('')
      try {
        const token = localStorage.getItem('authToken')
        const response = await fetch(`http://localhost:8080/divisions/${divisionId}`, {
          headers: {
            'Authorization': token || '',
            'Content-Type': 'application/json'
          }
        })
        console.log('Response status:', response.status)
        console.log('Response ok:', response.ok)
        
        if (!response.ok) {
          const errorText = await response.text()
          console.error('Error response:', errorText)
          throw new Error(`HTTP ${response.status}: ${errorText || 'Ошибка загрузки подразделения'}`)
        }
        
        const data = await response.json()
        console.log('Division data:', data)
        setDivision(data)
      } catch (e) {
        console.error('Ошибка загрузки подразделения:', e)
        setError(`${e.message}. URL: http://localhost:8080/divisions/${divisionId}`)
      } finally {
        setLoading(false)
      }
    }
    loadDivision()
  }, [divisionId])

  const handleBlockClick = (block) => {
    // Переходим на страницу целей и задач блока
    navigate(`/divisions/${divisionId}/blocks/${encodeURIComponent(block)}/goals`)
  }

  const handleBack = () => {
    navigate('/divisions')
  }

  return (
    <div className="app-card">
      <div className="app-card-header">
        <div className="app-card-title">
          {division ? `Блоки подразделения: ${division.name}` : 'Блоки подразделения'}
        </div>
        <div className="app-badge">ADMIN</div>
      </div>

      <table className="app-table">
        <thead>
          <tr>
            <th>№</th>
            <th>Ответственный исполнитель (блок)</th>
          </tr>
        </thead>
        <tbody>
          {!loading && division && division.blocks && division.blocks.map((block, index) => (
            <tr
              key={index}
              style={{ cursor: 'pointer' }}
              onClick={() => handleBlockClick(block)}
            >
              <td>{index + 1}</td>
              <td>{block}</td>
            </tr>
          ))}
          {!loading && division && (!division.blocks || division.blocks.length === 0) && (
            <tr>
              <td colSpan={2} style={{ fontSize: 13, color: '#9ca3af' }}>
                Нет блоков в этом подразделении
              </td>
            </tr>
          )}
        </tbody>
      </table>
      {loading && (
        <div style={{ marginTop: 12, fontSize: 13, color: '#9ca3af' }}>
          Загрузка блоков...
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

export default BlocksPage

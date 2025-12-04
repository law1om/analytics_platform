import { useState, useEffect } from 'react'

const GoalForm = ({ goal, divisionId, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    deadline: '',
    divisionId: divisionId,
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (goal) {
      setFormData({
        title: goal.title || '',
        description: goal.description || '',
        deadline: goal.deadline || '',
        divisionId: goal.divisionId || divisionId,
      })
    }
  }, [goal, divisionId])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')

    try {
      const token = localStorage.getItem('authToken')
      const url = goal
        ? `http://localhost:8080/goals/${goal.id}`
        : 'http://localhost:8080/goals'
      const method = goal ? 'PUT' : 'POST'

      const response = await fetch(url, {
        method,
        headers: {
          Authorization: token || '',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          ...formData,
          division: { id: formData.divisionId },
        }),
      })

      if (!response.ok) {
        throw new Error('Ошибка сохранения цели')
      }

      onSubmit()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="form">
      {error && <div className="form-error">{error}</div>}

      <div className="form-group">
        <label>Название цели *</label>
        <input
          type="text"
          name="title"
          value={formData.title}
          onChange={handleChange}
          required
          className="form-input"
        />
      </div>

      <div className="form-group">
        <label>Описание</label>
        <textarea
          name="description"
          value={formData.description}
          onChange={handleChange}
          rows={3}
          className="form-input"
        />
      </div>

      <div className="form-group">
        <label>Срок *</label>
        <input
          type="date"
          name="deadline"
          value={formData.deadline}
          onChange={handleChange}
          required
          className="form-input"
        />
      </div>

      <div className="form-actions">
        <button type="button" onClick={onCancel} disabled={loading} className="btn-secondary">
          Отмена
        </button>
        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? 'Сохранение...' : goal ? 'Обновить' : 'Создать'}
        </button>
      </div>
    </form>
  )
}

export default GoalForm

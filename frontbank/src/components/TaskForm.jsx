import { useState, useEffect } from 'react'

const TaskForm = ({ task, goals, users, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    expectedResult: '',
    actualResult: '',
    progress: 0,
    impact: '',
    status: 'PENDING',
    startDate: '',
    endDate: '',
    goalId: '',
    userId: '',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (task) {
      setFormData({
        title: task.title || '',
        description: task.description || '',
        expectedResult: task.expectedResult || '',
        actualResult: task.actualResult || '',
        progress: task.progress || 0,
        impact: task.impact || '',
        status: task.status || 'PENDING',
        startDate: task.startDate || '',
        endDate: task.endDate || '',
        goalId: task.goalId || '',
        userId: task.userId || '',
      })
    }
  }, [task])

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
      const url = task
        ? `http://localhost:8080/tasks/${task.id}`
        : 'http://localhost:8080/tasks'
      const method = task ? 'PUT' : 'POST'

      const payload = {
        ...formData,
        goal: formData.goalId ? { id: formData.goalId } : null,
        user: formData.userId ? { id: formData.userId } : null,
      }
      delete payload.goalId
      delete payload.userId

      const response = await fetch(url, {
        method,
        headers: {
          Authorization: token || '',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      })

      if (!response.ok) {
        throw new Error('Ошибка сохранения задачи')
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
        <label>Название задачи *</label>
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

      <div className="form-row">
        <div className="form-group">
          <label>Цель *</label>
          <select
            name="goalId"
            value={formData.goalId}
            onChange={handleChange}
            required
            className="form-input"
          >
            <option value="">Выберите цель</option>
            {goals.map((goal) => (
              <option key={goal.id} value={goal.id}>
                {goal.title}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Ответственный</label>
          <select
            name="userId"
            value={formData.userId}
            onChange={handleChange}
            className="form-input"
          >
            <option value="">Не назначен</option>
            {users.map((user) => (
              <option key={user.id} value={user.id}>
                {user.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label>Статус</label>
          <select
            name="status"
            value={formData.status}
            onChange={handleChange}
            className="form-input"
          >
            <option value="NOT_STARTED">NOT_STARTED</option>
            <option value="IN_PROGRESS">IN_PROGRESS</option>
            <option value="COMPLETED">COMPLETED</option>
            <option value="ON_HOLD">ON_HOLD</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
        </div>

        <div className="form-group">
          <label>Прогресс (%)</label>
          <input
            type="number"
            name="progress"
            value={formData.progress}
            onChange={handleChange}
            min="0"
            max="100"
            className="form-input"
          />
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label>Дата начала</label>
          <input
            type="date"
            name="startDate"
            value={formData.startDate}
            onChange={handleChange}
            className="form-input"
          />
        </div>

        <div className="form-group">
          <label>Дата окончания</label>
          <input
            type="date"
            name="endDate"
            value={formData.endDate}
            onChange={handleChange}
            className="form-input"
          />
        </div>
      </div>

      <div className="form-group">
        <label>Ожидаемый результат</label>
        <textarea
          name="expectedResult"
          value={formData.expectedResult}
          onChange={handleChange}
          rows={2}
          className="form-input"
        />
      </div>

      <div className="form-group">
        <label>Фактический результат</label>
        <textarea
          name="actualResult"
          value={formData.actualResult}
          onChange={handleChange}
          rows={2}
          className="form-input"
        />
      </div>

      <div className="form-group">
        <label>Эффект</label>
        <input
          type="text"
          name="impact"
          value={formData.impact}
          onChange={handleChange}
          className="form-input"
        />
      </div>

      <div className="form-actions">
        <button type="button" onClick={onCancel} disabled={loading} className="btn-secondary">
          Отмена
        </button>
        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? 'Сохранение...' : task ? 'Обновить' : 'Создать'}
        </button>
      </div>
    </form>
  )
}

export default TaskForm

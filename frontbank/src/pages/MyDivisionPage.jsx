import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { Bar, BarChart, ResponsiveContainer, Tooltip, XAxis, YAxis, CartesianGrid, Legend } from 'recharts'
import Modal from '../components/Modal'
import TaskForm from '../components/TaskForm'

function MyDivisionPage() {
  const { user } = useAuth()
  const [division, setDivision] = useState(null)
  const [goals, setGoals] = useState([])
  const [tasks, setTasks] = useState([])
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false)
  const [editingTask, setEditingTask] = useState(null)
  const [expandedTaskIds, setExpandedTaskIds] = useState(new Set())

  useEffect(() => {
    if (user?.divisionId) {
      loadData()
    }
  }, [user])

  const loadData = async () => {
    setLoading(true)
    setError('')
    try {
      const token = localStorage.getItem('authToken')
      const headers = { 'Authorization': token || '', 'Content-Type': 'application/json' }

      const [divRes, goalsRes, tasksRes, usersRes] = await Promise.all([
        fetch(`http://localhost:8080/divisions/${user.divisionId}`, { headers }),
        fetch(`http://localhost:8080/goals/division/${user.divisionId}`, { headers }),
        fetch(`http://localhost:8080/tasks`, { headers }),
        fetch(`http://localhost:8080/users`, { headers })
      ])

      if (!divRes.ok) throw new Error('Ошибка загрузки подразделения')

      const divData = await divRes.json()
      const goalsData = goalsRes.ok ? await goalsRes.json() : []
      const allTasks = tasksRes.ok ? await tasksRes.json() : []
      const usersData = usersRes.ok ? await usersRes.json() : []

      setDivision(divData)
      setGoals(goalsData)
      setUsers(usersData)

      const goalIds = goalsData.map(g => g.id)
      const divisionTasks = allTasks.filter(task => goalIds.includes(task.goalId))
      setTasks(divisionTasks)
    } catch (e) {
      setError(e.message || 'Ошибка загрузки данных')
    } finally {
      setLoading(false)
    }
  }

  const handleEditTask = (task) => {
    setEditingTask(task)
    setIsTaskModalOpen(true)
  }

  const handleDeleteTask = async (taskId) => {
    if (!window.confirm('Удалить эту задачу?')) return
    
    try {
      const token = localStorage.getItem('authToken')
      const response = await fetch(`http://localhost:8080/tasks/${taskId}`, {
        method: 'DELETE',
        headers: { 'Authorization': token || '' }
      })
      
      if (!response.ok) throw new Error('Ошибка удаления задачи')
      
      loadData()
    } catch (e) {
      setError(e.message)
    }
  }

  const toggleTaskDescription = (taskId) => {
    setExpandedTaskIds(prev => {
      const newSet = new Set(prev)
      if (newSet.has(taskId)) {
        newSet.delete(taskId)
      } else {
        newSet.add(taskId)
      }
      return newSet
    })
  }

  const chartData = goals.map(goal => {
    const goalTasks = tasks.filter(t => t.goalId === goal.id)
    const completed = goalTasks.filter(t => t.status === 'COMPLETED').length
    const pending = goalTasks.length - completed
    
    return {
      name: goal.title,
      completed,
      pending
    }
  })

  if (loading) {
    return <div style={{ fontSize: 13, color: '#9ca3af' }}>Загрузка...</div>
  }

  if (error) {
    return <div className="form-error">{error}</div>
  }

  if (!division) {
    return <div className="form-error">Подразделение не найдено</div>
  }

  return (
    <div>
      <h2>{division.name}</h2>
      <p style={{ color: '#6b7280', marginBottom: 24 }}>{division.description || 'Ваше подразделение'}</p>

  
      {chartData.length > 0 && (
        <div className="app-card" style={{ marginBottom: 24 }}>
          <div className="app-card-header">
            <div className="app-card-title">Задачи по целям</div>
            <div className="app-badge">Выполненные / В работе</div>
          </div>
          <div style={{ width: '100%', height: 300 }}>
            <ResponsiveContainer>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis dataKey="name" stroke="#6b7280" fontSize={11} />
                <YAxis stroke="#6b7280" fontSize={11} />
                <Tooltip />
                <Legend />
                <Bar dataKey="completed" fill="#22c55e" name="Выполнено" />
                <Bar dataKey="pending" fill="#f59e0b" name="В работе" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      )}

    
      <h3>Цели</h3>
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
          {goals.length === 0 && (
            <tr>
              <td colSpan={3} style={{ fontSize: 13, color: '#9ca3af' }}>
                Нет целей
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {/* Задачи */}
      <h3 style={{ marginTop: 24 }}>Мои задачи</h3>
      <table className="app-table">
        <thead>
          <tr>
            <th>Задача</th>
            <th>Цель</th>
            <th>Статус</th>
            <th>Прогресс</th>
            <th>Начало</th>
            <th>Окончание</th>
            <th style={{ width: 120 }}>Действия</th>
          </tr>
        </thead>
        <tbody>
          {tasks.map((task) => {
            const isExpanded = expandedTaskIds.has(task.id)
            const hasDescription = task.description && task.description.trim().length > 0
            
            return (
              <>
                <tr key={task.id}>
                  <td 
                    onClick={() => hasDescription && toggleTaskDescription(task.id)}
                    style={{
                      cursor: hasDescription ? 'pointer' : 'default'
                    }}
                  >
                    <span style={{ display: 'flex', alignItems: 'center', gap: '6px'}}>
                      {task.title}
                    </span>
                  </td>
                  <td>{task.goalTitle || '—'}</td>
                  <td>{task.status}</td>
                  <td>{task.progress ?? 0}%</td>
                  <td>{task.startDate || '—'}</td>
                  <td>{task.endDate || '—'}</td>
                  <td>
                    <button onClick={() => handleEditTask(task)} className="btn-action">
                      Редактировать
                    </button>
                    <button onClick={() => handleDeleteTask(task.id)} className="btn-action-delete">
                      Удалить
                    </button>
                  </td>
                </tr>
                {isExpanded && hasDescription && (
                  <tr key={`${task.id}-description`}>
                    <td colSpan={7} style={{
                      backgroundColor: '#ededef',
                      padding: '12px 16px',
                      borderTop: 'none'
                    }}>
                      <div style={{
                        fontSize: '13px',
                        color: '#090909',
                        whiteSpace: 'pre-wrap',
                        lineHeight: '1.5'
                      }}>
                        <strong style={{ color: '#0b0b0b', marginBottom: '4px', display: 'block' }}>Описание:</strong>
                        {task.description}
                      </div>
                    </td>
                  </tr>
                )}
              </>
            )
          })}
          {tasks.length === 0 && (
            <tr>
              <td colSpan={7} style={{ fontSize: 13, color: '#9ca3af' }}>
                Нет задач
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <Modal
        isOpen={isTaskModalOpen}
        onClose={() => setIsTaskModalOpen(false)}
        title="Редактировать задачу"
      >
        <TaskForm
          task={editingTask}
          goals={goals}
          users={users}
          onSubmit={() => {
            setIsTaskModalOpen(false)
            loadData()
          }}
          onCancel={() => setIsTaskModalOpen(false)}
        />
      </Modal>
    </div>
  )
}

export default MyDivisionPage

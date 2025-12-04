import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import Modal from '../components/Modal'
import GoalForm from '../components/GoalForm'
import TaskForm from '../components/TaskForm'

function BlockGoalsPage() {
  const { divisionId, blockName } = useParams()
  const navigate = useNavigate()
  const [division, setDivision] = useState(null)
  const [goals, setGoals] = useState([])
  const [tasks, setTasks] = useState([])
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  
  const [isGoalModalOpen, setIsGoalModalOpen] = useState(false)
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false)
  const [editingGoal, setEditingGoal] = useState(null)
  const [editingTask, setEditingTask] = useState(null)
  const [expandedTaskIds, setExpandedTaskIds] = useState(new Set())

  useEffect(() => {
    async function loadData() {
      setLoading(true)
      setError('')
      try {
        const token = localStorage.getItem('authToken')
        const headers = {
          'Authorization': token || '',
          'Content-Type': 'application/json'
        }

        // Загружаем подразделение
        const divResponse = await fetch(`http://localhost:8080/divisions/${divisionId}`, { headers })
        if (!divResponse.ok) {
          throw new Error('Ошибка загрузки подразделения')
        }
        const divData = await divResponse.json()
        setDivision(divData)

        // Загружаем все цели подразделения
        const goalsResponse = await fetch(`http://localhost:8080/goals/division/${divisionId}`, { headers })
        if (!goalsResponse.ok) {
          throw new Error('Ошибка загрузки целей')
        }
        const goalsData = await goalsResponse.json()
        setGoals(goalsData)

        // Загружаем все задачи
        const tasksResponse = await fetch(`http://localhost:8080/tasks`, { headers })
        if (tasksResponse.ok) {
          const allTasks = await tasksResponse.json()
          
          // Фильтруем задачи по целям этого подразделения
          const goalIds = goalsData.map(g => g.id)
          const divisionTasks = allTasks.filter(task => goalIds.includes(task.goalId))
          
          setTasks(divisionTasks)
        }

        // Загружаем пользователей
        const usersResponse = await fetch(`http://localhost:8080/users`, { headers })
        if (usersResponse.ok) {
          const usersData = await usersResponse.json()
          setUsers(usersData)
        }
      } catch (e) {
        setError(e.message || 'Ошибка загрузки данных')
      } finally {
        setLoading(false)
      }
    }
    loadData()
  }, [divisionId, blockName])

  const handleBack = () => {
    navigate(`/divisions/${divisionId}/blocks`)
  }

  const reloadData = async () => {
    setLoading(true)
    setError('')
    try {
      const token = localStorage.getItem('authToken')
      const headers = {
        'Authorization': token || '',
        'Content-Type': 'application/json'
      }

      
      const divResponse = await fetch(`http://localhost:8080/divisions/${divisionId}`, { headers })
      if (!divResponse.ok) {
        throw new Error('Ошибка загрузки подразделения')
      }
      const divData = await divResponse.json()
      setDivision(divData)

      
      const goalsResponse = await fetch(`http://localhost:8080/goals/division/${divisionId}`, { headers })
      if (!goalsResponse.ok) {
        throw new Error('Ошибка загрузки целей')
      }
      const goalsData = await goalsResponse.json()
      setGoals(goalsData)

      
      const tasksResponse = await fetch(`http://localhost:8080/tasks`, { headers })
      if (tasksResponse.ok) {
        const allTasks = await tasksResponse.json()
        
        const goalIds = goalsData.map(g => g.id)
        const divisionTasks = allTasks.filter(task => goalIds.includes(task.goalId))
        
        setTasks(divisionTasks)
      }

      const usersResponse = await fetch(`http://localhost:8080/users`, { headers })
      if (usersResponse.ok) {
        const usersData = await usersResponse.json()
        setUsers(usersData)
      }
    } catch (e) {
      setError(e.message || 'Ошибка загрузки данных')
    } finally {
      setLoading(false)
    }
  }

  const handleCreateGoal = () => {
    setEditingGoal(null)
    setIsGoalModalOpen(true)
  }

  const handleEditGoal = (goal) => {
    setEditingGoal(goal)
    setIsGoalModalOpen(true)
  }

  const handleDeleteGoal = async (goalId) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту цель?')) return

    try {
      const token = localStorage.getItem('authToken')
      const response = await fetch(`http://localhost:8080/goals/${goalId}`, {
        method: 'DELETE',
        headers: {
          Authorization: token || '',
        },
      })

      if (response.ok) {
        reloadData()
      } else {
        alert('Ошибка удаления цели')
      }
    } catch (err) {
      alert('Ошибка удаления цели')
    }
  }

  const handleCreateTask = () => {
    setEditingTask(null)
    setIsTaskModalOpen(true)
  }

  const handleEditTask = (task) => {
    setEditingTask(task)
    setIsTaskModalOpen(true)
  }

  const handleDeleteTask = async (taskId) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту задачу?')) return

    try {
      const token = localStorage.getItem('authToken')
      const response = await fetch(`http://localhost:8080/tasks/${taskId}`, {
        method: 'DELETE',
        headers: {
          Authorization: token || '',
        },
      })

      if (response.ok) {
        reloadData()
      } else {
        alert('Ошибка удаления задачи')
      }
    } catch (err) {
      alert('Ошибка удаления задачи')
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

  const decodedBlockName = decodeURIComponent(blockName)

  return (
    <div className="app-card">
      <div className="app-card-header">
        <div className="app-card-title">
          {division ? `${division.name} / ${decodedBlockName}` : 'Цели и задачи блока'}
        </div>
        <div className="app-badge">ADMIN</div>
      </div>

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

      {!loading && (
        <>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
            <h3 style={{ margin: 0 }}>Цели (Инициативы)</h3>
            <button onClick={handleCreateGoal} className="btn-primary">
              + Создать цель
            </button>
          </div>
          <table className="app-table">
            <thead>
              <tr>
                <th>Цель</th>
                <th>Срок</th>
                <th>Прогресс</th>
                <th style={{ width: 120 }}>Действия</th>
              </tr>
            </thead>
            <tbody>
              {goals.map((goal) => (
                <tr key={goal.id}>
                  <td>{goal.title}</td>
                  <td>{goal.deadline ?? '—'}</td>
                  <td>{goal.progress ?? 0}%</td>
                  <td>
                    <button onClick={() => handleEditGoal(goal)} className="btn-action">
                      Редактировать
                    </button>
                    <button onClick={() => handleDeleteGoal(goal.id)} className="btn-action-delete">
                      Удалить
                    </button>
                  </td>
                </tr>
              ))}
              {goals.length === 0 && (
                <tr>
                  <td colSpan={4} style={{ fontSize: 13, color: '#9ca3af' }}>
                    Нет целей для этого блока
                  </td>
                </tr>
              )}
            </tbody>
          </table>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 24, marginBottom: 8 }}>
            <h3 style={{ margin: 0 }}>Задачи</h3>
            <button onClick={handleCreateTask} className="btn-primary">
              + Создать задачу
            </button>
          </div>
          <table className="app-table">
            <thead>
              <tr>
                <th>Задача</th>
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
                        <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                          
                          {task.title}
                        </span>
                      </td>
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
                        <td colSpan={6} style={{
                          backgroundColor: '#f9fafb',
                          padding: '12px 16px',
                          borderTop: 'none'
                        }}>
                          <div style={{
                            fontSize: '13px',
                            color: '#374151',
                            whiteSpace: 'pre-wrap',
                            lineHeight: '1.5'
                          }}>
                            <strong style={{ color: '#6b7280', marginBottom: '4px', display: 'block' }}>Описание:</strong>
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
                  <td colSpan={6} style={{ fontSize: 13, color: '#9ca3af' }}>
                    Нет задач для этого блока
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </>
      )}

    
      <Modal
        isOpen={isGoalModalOpen}
        onClose={() => setIsGoalModalOpen(false)}
        title={editingGoal ? 'Редактировать цель' : 'Создать цель'}
      >
        <GoalForm
          goal={editingGoal}
          divisionId={divisionId}
          onSubmit={() => {
            setIsGoalModalOpen(false)
            reloadData()
          }}
          onCancel={() => setIsGoalModalOpen(false)}
        />
      </Modal>

      <Modal
        isOpen={isTaskModalOpen}
        onClose={() => setIsTaskModalOpen(false)}
        title={editingTask ? 'Редактировать задачу' : 'Создать задачу'}
      >
        <TaskForm
          task={editingTask}
          goals={goals}
          users={users}
          onSubmit={() => {
            setIsTaskModalOpen(false)
            reloadData()
          }}
          onCancel={() => setIsTaskModalOpen(false)}
        />
      </Modal>
    </div>
  )
}

export default BlockGoalsPage

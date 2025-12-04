import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import {
  Bar,
  BarChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
  CartesianGrid,
  Legend,
} from 'recharts'

function Dashboard() {
  const { user } = useAuth()
  const [stats, setStats] = useState({ totalTasks: 0, completedTasks: 0, pendingTasks: 0, totalGoals: 0, completedGoals: 0 })
  const [divisionGoalStats, setDivisionGoalStats] = useState([])
  const [divisionTaskStats, setDivisionTaskStats] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function load() {
      setLoading(true)
      setError('')
      try {
        const token = localStorage.getItem('authToken')
        const headers = { 'Authorization': token || '', 'Content-Type': 'application/json' }

        const [tasksRes, goalsRes, divisionsRes] = await Promise.all([
          fetch('http://localhost:8080/tasks', { headers }),
          fetch('http://localhost:8080/goals', { headers }),
          fetch('http://localhost:8080/divisions', { headers })
        ])

        if (!tasksRes.ok || !goalsRes.ok || !divisionsRes.ok) throw new Error('Ошибка загрузки данных')

        const tasks = await tasksRes.json()
        const goals = await goalsRes.json()
        const divisions = await divisionsRes.json()

        const completedTasks = tasks.filter(t => t.status === 'COMPLETED').length
        const completedGoals = goals.filter(g => g.progress === 100).length
        
        setStats({
          totalTasks: tasks.length,
          completedTasks,
          pendingTasks: tasks.length - completedTasks,
          totalGoals: goals.length,
          completedGoals
        })

        const goalStats = divisions.map(div => {
          const divGoals = goals.filter(g => g.divisionId === div.id)
          const completed = divGoals.filter(g => g.progress === 100).length
          const inProgress = divGoals.length - completed
          
          return {
            name: div.name,
            completed,
            inProgress,
            total: divGoals.length
          }
        }).filter(stat => stat.total > 0)

        setDivisionGoalStats(goalStats)

        // Статистика задач по отделам
        const taskStats = divisions.map(div => {
          const divGoalIds = goals.filter(g => g.divisionId === div.id).map(g => g.id)
          const divTasks = tasks.filter(t => divGoalIds.includes(t.goalId))
          const completed = divTasks.filter(t => t.status === 'COMPLETED').length
          const pending = divTasks.length - completed
          
          return {
            name: div.name,
            completed,
            pending,
            total: divTasks.length
          }
        }).filter(stat => stat.total > 0)

        setDivisionTaskStats(taskStats)
      } catch (e) {
        setError(e.message || 'Ошибка загрузки')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  if (loading) {
    return <div style={{ fontSize: 13, color: '#9ca3af' }}>Загрузка...</div>
  }

  if (error) {
    return <div className="form-error">{error}</div>
  }

  return (
    <div>
      <div className="app-grid-tiles">
        <div className="app-tile">
          <div className="app-tile-label">Всего целей</div>
          <div className="app-tile-value">{stats.totalGoals}</div>
        </div>
        <div className="app-tile">
          <div className="app-tile-label">Целей выполнено</div>
          <div className="app-tile-value">{stats.completedGoals}</div>
        </div>
        <div className="app-tile">
          <div className="app-tile-label">Всего задач</div>
          <div className="app-tile-value">{stats.totalTasks}</div>
        </div>
        <div className="app-tile">
          <div className="app-tile-label">Задач выполнено</div>
          <div className="app-tile-value">{stats.completedTasks}</div>
        </div>
      </div>

      <div className="app-card" style={{ marginTop: 20 }}>
        <div className="app-card-header">
          <div className="app-card-title">Цели по отделам</div>
          <div className="app-badge">Выполненные / В работе</div>
        </div>
        <div style={{ width: '100%', height: 350 }}>
          <ResponsiveContainer>
            <BarChart data={divisionGoalStats}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="name" stroke="#6b7280" fontSize={11} />
              <YAxis stroke="#6b7280" fontSize={11} />
              <Tooltip />
              <Legend />
              <Bar dataKey="completed" fill="#10b981" name="Выполнено" />
              <Bar dataKey="inProgress" fill="#3b82f6" name="В работе" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="app-card" style={{ marginTop: 20 }}>
        <div className="app-card-header">
          <div className="app-card-title">Задачи по отделам</div>
          <div className="app-badge">Выполненные / В работе</div>
        </div>
        <div style={{ width: '100%', height: 350 }}>
          <ResponsiveContainer>
            <BarChart data={divisionTaskStats}>
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
    </div>
  )
}

export default Dashboard

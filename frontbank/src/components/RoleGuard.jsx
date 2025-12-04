import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function RoleGuard({ roles }) {
  const { user } = useAuth()

  if (!user || (roles && !roles.includes(user.role))) {
    return <Navigate to="/dashboard" replace />
  }

  return <Outlet />
}

export default RoleGuard

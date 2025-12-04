import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import Dashboard from './pages/Dashboard'
import ExcelUploadPage from './pages/ExcelUploadPage'
import DivisionsPage from './pages/DivisionsPage'
import BlocksPage from './pages/BlocksPage'
import BlockGoalsPage from './pages/BlockGoalsPage'
import GoalsPage from './pages/GoalsPage'
import MyDivisionPage from './pages/MyDivisionPage'
import NotFoundPage from './pages/NotFoundPage'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import RoleGuard from './components/RoleGuard'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/my-division" element={<MyDivisionPage />} />
          <Route element={<RoleGuard roles={["ADMIN"]} />}>
            <Route path="/upload" element={<ExcelUploadPage />} />
            <Route path="/divisions" element={<DivisionsPage />} />
            <Route path="/divisions/:divisionId/blocks" element={<BlocksPage />} />
            <Route path="/divisions/:divisionId/blocks/:blockName/goals" element={<BlockGoalsPage />} />
            <Route path="/divisions/:divisionId/goals" element={<GoalsPage />} />
          </Route>
        </Route>
      </Route>
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}

export default App

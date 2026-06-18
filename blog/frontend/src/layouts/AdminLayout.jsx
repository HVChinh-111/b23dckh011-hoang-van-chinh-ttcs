import { Navigate, Outlet } from 'react-router-dom'
import AdminSidebar from '../components/admin/AdminSidebar.jsx'
import { isAuthenticated } from '../utils/auth.js'

const AdminLayout = () => {
  if (!isAuthenticated()) return <Navigate to="/admin/login" replace />

  return (
    <div className="flex h-screen overflow-hidden bg-primary-container">
      <AdminSidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Outlet />
      </div>
    </div>
  )
}

export default AdminLayout

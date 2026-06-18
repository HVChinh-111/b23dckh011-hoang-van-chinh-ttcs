import { Link, useLocation, useNavigate } from 'react-router-dom'
import FileText from 'lucide-react/dist/esm/icons/file-text'
import Tag from 'lucide-react/dist/esm/icons/tag'
import User from 'lucide-react/dist/esm/icons/user'
import Settings from 'lucide-react/dist/esm/icons/settings'
import LogOut from 'lucide-react/dist/esm/icons/log-out'
import { logout } from '../../utils/auth.js'

const NAV_ITEMS = [
  { label: 'Bài viết', to: '/admin/posts', matchPrefix: '/admin/posts', Icon: FileText },
  { label: 'Chủ đề & Series', to: '/admin/topics-series', matchPrefix: '/admin/topics-series', Icon: Tag },
  { label: 'Hồ sơ', to: '/admin/profile', matchPrefix: '/admin/profile', Icon: User },
]

const INACTIVE_CLS =
  'flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container-highest rounded-xl transition-all duration-200 font-label-md text-label-md w-full'
const ACTIVE_CLS =
  'flex items-center gap-3 px-4 py-3 bg-primary-container text-on-primary-container rounded-xl font-bold translate-x-1 font-label-md text-label-md w-full'
const AdminSidebar = () => {
  const { pathname } = useLocation()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/admin/login')
  }

  return (
    <aside className="hidden md:flex flex-col h-screen p-4 bg-surface-white border-r border-outline-variant w-64 flex-shrink-0 z-20 shadow-sm sticky top-0">
      {/* Logo */}
      <div className="mb-6 px-2 flex items-center gap-3">
        <div className="w-10 h-10 rounded-full bg-primary-container flex items-center justify-center text-on-primary-container font-bold text-lg flex-shrink-0">
          H
        </div>
        <div>
          <h1 className="font-display-lg text-headline-md font-bold text-on-surface leading-tight">HVChinh</h1>
          <p className="font-label-sm text-label-sm text-on-surface-variant">Quản trị viên</p>
        </div>
      </div>

      {/* Section label */}
      <div className="px-2 mb-3">
        <p className="text-xs font-bold uppercase tracking-widest text-on-surface-variant/60">
          Bảng điều khiển
        </p>
        <div className="mt-2 border-b border-outline-variant/50" />
      </div>

      {/* Main nav */}
      <div className="flex flex-col gap-1 mt-2">
        {NAV_ITEMS.map(({ label, to, matchPrefix, Icon }) => {
          const isActive = pathname.startsWith(matchPrefix)
          return (
            <Link key={label} to={to} className={isActive ? ACTIVE_CLS : INACTIVE_CLS}>
              <Icon size={22} />
              {label}
            </Link>
          )
        })}
      </div>

      {/* Bottom */}
      <div className="flex flex-col gap-1 mt-auto pt-4 border-t border-outline-variant">
        <button type="button" className={`${INACTIVE_CLS} text-left`}>
          <Settings size={22} />
          Cài đặt
        </button>
        <button type="button" onClick={handleLogout} className={`${INACTIVE_CLS} text-left`}>
          <LogOut size={22} />
          Đăng xuất
        </button>
      </div>
    </aside>
  )
}

export default AdminSidebar

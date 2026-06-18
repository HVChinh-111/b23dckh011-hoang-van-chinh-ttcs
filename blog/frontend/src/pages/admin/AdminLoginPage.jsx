import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Mail from 'lucide-react/dist/esm/icons/mail'
import Lock from 'lucide-react/dist/esm/icons/lock'
import Eye from 'lucide-react/dist/esm/icons/eye'
import EyeOff from 'lucide-react/dist/esm/icons/eye-off'
import AlertCircle from 'lucide-react/dist/esm/icons/alert-circle'
import ArrowLeft from 'lucide-react/dist/esm/icons/arrow-left'
import { login } from '../../utils/auth.js'

const AdminLoginPage = () => {
  const navigate = useNavigate()

  useEffect(() => {
    document.title = 'Đăng nhập | Admin - HVChinh Blog'
  }, [])
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!email.trim() || !password.trim()) {
      setError('Vui lòng nhập đầy đủ email và mật khẩu')
      return
    }
    setError(null)
    setLoading(true)
    try {
      await login(email, password)
      navigate('/admin/posts')
    } catch (err) {
      setError(err.message ?? 'Email hoặc mật khẩu không đúng')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="bg-primary-container min-h-screen flex items-center justify-center p-margin-mobile md:p-margin-desktop antialiased text-on-surface relative overflow-hidden">
      {/* Decorative blurs */}
      <div className="absolute top-10 left-10 w-48 h-48 bg-secondary-container/20 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-10 right-10 w-64 h-64 bg-surface-white/20 rounded-full blur-3xl pointer-events-none" />

      <div
        className="w-full max-w-md rounded-xl border border-white/50 relative overflow-hidden p-8 md:p-12"
        style={{ background: 'rgba(255,255,255,0.95)', backdropFilter: 'blur(16px)', boxShadow: '0 20px 40px rgba(0,0,0,0.08)' }}
      >
        {/* Decorative corner */}
        <div className="absolute top-0 right-0 w-32 h-32 bg-secondary-container/20 rounded-bl-full -z-10 blur-xl" />
        <div className="absolute bottom-0 left-0 w-24 h-24 bg-primary-container/30 rounded-tr-full -z-10 blur-lg" />

        {/* Header */}
        <div className="text-center mb-10">
          <h1 className="font-display-lg text-headline-md md:text-headline-lg font-extrabold text-on-background tracking-tight mb-2">
            HVChinh Admin
          </h1>
          <p className="font-body-md text-body-md text-on-surface-variant">Quản trị hệ thống nội dung</p>
        </div>

        {/* Error message */}
        {error ? (
          <div className="mb-6 bg-error-container text-on-error-container font-label-md text-label-md p-4 rounded-lg flex items-start gap-3 border border-error/20">
            <AlertCircle size={20} className="shrink-0 mt-0.5" />
            <span>{error}</span>
          </div>
        ) : null}

        {/* Form */}
        <form className="space-y-6" onSubmit={handleSubmit}>
          {/* Email */}
          <div className="space-y-2">
            <label className="block font-label-md text-label-md text-on-surface" htmlFor="email">
              Email
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <Mail size={20} className="text-outline" />
              </div>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@hvchinh.com"
                className="block w-full pl-11 pr-4 py-3 bg-surface-white border border-outline-variant/30 rounded-lg text-on-surface font-body-md text-body-md focus:ring-2 focus:ring-on-background focus:border-on-background transition-colors placeholder:text-outline/50 shadow-sm outline-none"
              />
            </div>
          </div>

          {/* Password */}
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <label className="block font-label-md text-label-md text-on-surface" htmlFor="password">
                Mật khẩu
              </label>
              <span className="font-label-sm text-label-sm text-on-surface-variant cursor-pointer hover:text-on-background transition-colors">
                Quên mật khẩu?
              </span>
            </div>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <Lock size={20} className="text-outline" />
              </div>
              <input
                id="password"
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="block w-full pl-11 pr-12 py-3 bg-surface-white border border-outline-variant/30 rounded-lg text-on-surface font-body-md text-body-md focus:ring-2 focus:ring-on-background focus:border-on-background transition-colors placeholder:text-outline/50 shadow-sm outline-none"
              />
              <button
                type="button"
                onClick={() => setShowPassword((prev) => !prev)}
                className="absolute inset-y-0 right-0 pr-4 flex items-center text-outline hover:text-on-surface transition-colors"
              >
                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
          </div>

          {/* Submit */}
          <button
            type="submit"
            disabled={loading}
            className="w-full flex justify-center py-3.5 px-4 rounded-xl font-label-md text-body-md font-bold text-on-secondary-container bg-secondary-container hover:bg-secondary-fixed-dim transition-all duration-200 focus:outline-none active:scale-[0.98] disabled:opacity-60 disabled:cursor-not-allowed"
          >
            {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>

        {/* Back link */}
        <div className="mt-8 text-center">
          <Link
            to="/"
            className="font-label-sm text-label-sm text-on-surface-variant hover:text-on-background transition-colors flex items-center justify-center gap-1 group"
          >
            <ArrowLeft size={16} className="group-hover:-translate-x-1 transition-transform" />
            Trở về trang chủ
          </Link>
        </div>
      </div>
    </div>
  )
}

export default AdminLoginPage

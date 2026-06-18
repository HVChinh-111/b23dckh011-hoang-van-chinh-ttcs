import { useState, useRef, useEffect } from 'react'
import Camera from 'lucide-react/dist/esm/icons/camera'
import Mail from 'lucide-react/dist/esm/icons/mail'
import Code2 from 'lucide-react/dist/esm/icons/code-2'
import Globe from 'lucide-react/dist/esm/icons/globe'
import ExternalLink from 'lucide-react/dist/esm/icons/external-link'
import CheckCircle from 'lucide-react/dist/esm/icons/check-circle'
import AdminTopbar from '../../components/admin/AdminTopbar.jsx'
import { updateAdminProfile } from '../../lib/fetchModelData.js'

const AdminProfilePage = () => {
  const [avatarSrc, setAvatarSrc] = useState(null)
  const [avatarFile, setAvatarFile] = useState(null)
  const [name, setName] = useState('')
  const [bio, setBio] = useState('')
  const [email, setEmail] = useState('')
  const [github, setGithub] = useState('')
  const [linkedin, setLinkedin] = useState('')
  const [facebook, setFacebook] = useState('')
  const [showToast, setShowToast] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const fileInputRef = useRef(null)

  useEffect(() => {
    document.title = 'Hồ sơ | Admin - HVChinh Blog'
  }, [])

  useEffect(() => {
    fetch('/api/profile')
      .then((res) => res.json())
      .then(({ data }) => {
        setName(data.fullName ?? '')
        setBio(data.shortBio ?? '')
        setEmail(data.contactEmail ?? '')
        setGithub(data.githubUrl ?? '')
        setLinkedin(data.linkedInUrl ?? '')
        setFacebook(data.facebookUrl ?? '')
        if (data.avatar?.publicPath) {
          setAvatarSrc(data.avatar.publicPath)
        }
      })
      .catch(() => {})
  }, [])

  const handleAvatarChange = (e) => {
    const file = e.target.files?.[0]
    if (!file) return
    setAvatarFile(file)
    const reader = new FileReader()
    reader.onload = (ev) => setAvatarSrc(ev.target.result)
    reader.readAsDataURL(file)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    setError(null)
    const formData = new FormData()
    const profileJson = JSON.stringify({
      fullName: name,
      shortBio: bio,
      contactEmail: email,
      githubUrl: github,
      linkedInUrl: linkedin,
      facebookUrl: facebook,
    })
    formData.append('data', new Blob([profileJson], { type: 'application/json' }))
    if (avatarFile) formData.append('avatar', avatarFile)
    try {
      await updateAdminProfile(formData)
      setShowToast(true)
      setAvatarFile(null)
      setTimeout(() => setShowToast(false), 3000)
    } catch (err) {
      setError(err.message ?? 'Lưu thất bại')
    } finally {
      setSaving(false)
    }
  }

  const inputCls =
    'w-full bg-surface-white border border-outline-variant/50 rounded-xl px-4 py-3 font-body-md text-body-md text-on-background focus:outline-none focus:border-on-background focus:ring-1 focus:ring-on-background transition-colors placeholder:text-on-surface-variant/50'

  const iconInputCls =
    'w-full bg-surface-white border border-outline-variant/50 rounded-xl pl-12 pr-4 py-3 font-body-md text-body-md text-on-background focus:outline-none focus:border-on-background focus:ring-1 focus:ring-on-background transition-colors'

  return (
    <>
      <AdminTopbar title="Quản lý Hồ sơ" />

      <div className="flex-1 overflow-y-auto p-margin-mobile md:p-margin-desktop flex flex-col items-center">
        <div className="w-full max-w-[860px]">
          {error ? (
            <div className="mb-6 bg-error-container text-on-error-container font-label-md text-label-md p-4 rounded-xl border border-error/20">
              {error}
            </div>
          ) : null}

          <div className="bg-surface-white rounded-xl shadow-[0_10px_25px_rgba(0,0,0,0.04)] p-6 md:p-8 border border-transparent hover:border-outline-variant/20 transition-all duration-300">
            <form className="space-y-8" onSubmit={handleSubmit}>

              {/* Avatar section */}
              <div className="flex flex-col md:flex-row items-start gap-6 pb-8 border-b border-outline-variant/30">
                <div className="flex-shrink-0">
                  <div
                    className="relative w-32 h-32 group cursor-pointer"
                    onClick={() => fileInputRef.current?.click()}
                  >
                    {avatarSrc ? (
                      <img
                        src={avatarSrc}
                        alt="Avatar"
                        className="w-full h-full rounded-full object-cover border-4 border-surface-white shadow-sm transition-transform duration-300 group-hover:scale-105"
                      />
                    ) : (
                      <div className="w-full h-full rounded-full bg-surface-container-high border-4 border-surface-white shadow-sm flex items-center justify-center text-on-surface-variant">
                        <Camera size={32} />
                      </div>
                    )}
                    <div className="absolute inset-0 bg-on-background/50 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                      <Camera size={32} className="text-surface-white" />
                    </div>
                    <input
                      ref={fileInputRef}
                      type="file"
                      accept="image/*"
                      className="hidden"
                      onChange={handleAvatarChange}
                    />
                  </div>
                </div>
                <div className="flex-1">
                  <h3 className="font-headline-md text-headline-md text-on-background mb-2">Ảnh đại diện</h3>
                  <p className="font-body-md text-body-md text-on-surface-variant mb-4">
                    Khuyến nghị ảnh vuông, kích thước tối thiểu 256×256px. Hỗ trợ JPG, PNG, WebP. Tối đa 2MB.
                  </p>
                  <button
                    type="button"
                    onClick={() => fileInputRef.current?.click()}
                    className="bg-surface-white border border-outline text-on-surface font-label-md text-label-md py-2 px-4 rounded-xl hover:bg-surface-container-low transition-colors duration-200"
                  >
                    Tải ảnh lên
                  </button>
                </div>
              </div>

              {/* Basic info */}
              <div className="grid grid-cols-1 gap-6">
                <div className="space-y-2">
                  <label className="block font-label-md text-label-md text-on-background" htmlFor="fullName">
                    Họ tên
                  </label>
                  <input
                    id="fullName"
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Nhập họ tên của bạn"
                    className={inputCls}
                  />
                </div>
                <div className="space-y-2">
                  <label className="block font-label-md text-label-md text-on-background" htmlFor="bio">
                    Giới thiệu ngắn
                  </label>
                  <textarea
                    id="bio"
                    rows={4}
                    value={bio}
                    onChange={(e) => setBio(e.target.value)}
                    placeholder="Viết một vài dòng giới thiệu về bản thân..."
                    className={`${inputCls} resize-y`}
                  />
                </div>
              </div>

              {/* Contact & social */}
              <div className="pt-8 border-t border-outline-variant/30 space-y-6">
                <h3 className="font-headline-md text-headline-md text-on-background">Liên hệ &amp; Mạng xã hội</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-2">
                    <label className="block font-label-md text-label-md text-on-background" htmlFor="email">
                      Email liên hệ
                    </label>
                    <div className="relative">
                      <Mail size={20} className="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant" />
                      <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="email@example.com"
                        className={iconInputCls}
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <label className="block font-label-md text-label-md text-on-background" htmlFor="github">
                      GitHub URL
                    </label>
                    <div className="relative">
                      <Code2 size={20} className="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant" />
                      <input
                        id="github"
                        type="url"
                        value={github}
                        onChange={(e) => setGithub(e.target.value)}
                        placeholder="https://github.com/username"
                        className={iconInputCls}
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <label className="block font-label-md text-label-md text-on-background" htmlFor="linkedin">
                      LinkedIn URL
                    </label>
                    <div className="relative">
                      <Globe size={20} className="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant" />
                      <input
                        id="linkedin"
                        type="url"
                        value={linkedin}
                        onChange={(e) => setLinkedin(e.target.value)}
                        placeholder="https://linkedin.com/in/username"
                        className={iconInputCls}
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <label className="block font-label-md text-label-md text-on-background" htmlFor="facebook">
                      Facebook URL
                    </label>
                    <div className="relative">
                      <ExternalLink size={20} className="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant" />
                      <input
                        id="facebook"
                        type="url"
                        value={facebook}
                        onChange={(e) => setFacebook(e.target.value)}
                        placeholder="https://facebook.com/username"
                        className={iconInputCls}
                      />
                    </div>
                  </div>
                </div>
              </div>

              {/* Actions */}
              <div className="pt-8 flex flex-col sm:flex-row items-center justify-end gap-4 border-t border-outline-variant/30">
                <button
                  type="button"
                  onClick={() => {}}
                  className="w-full sm:w-auto bg-surface-white border border-outline text-on-surface font-label-md text-label-md py-3 px-6 rounded-xl hover:bg-surface-container-low transition-colors duration-200"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="w-full sm:w-auto bg-on-background text-surface-white font-label-md text-label-md py-3 px-8 rounded-xl hover:opacity-90 transition-opacity duration-200 shadow-md disabled:opacity-60 disabled:cursor-not-allowed"
                >
                  {saving ? 'Đang lưu...' : 'Lưu cập nhật'}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      {/* Success toast */}
      {showToast ? (
        <div className="fixed bottom-4 right-4 md:bottom-8 md:right-8 bg-status-success text-surface-white px-6 py-4 rounded-xl shadow-lg flex items-center gap-3 z-50">
          <CheckCircle size={20} />
          <span className="font-label-md text-label-md">Cập nhật thành công</span>
        </div>
      ) : null}
    </>
  )
}

export default AdminProfilePage

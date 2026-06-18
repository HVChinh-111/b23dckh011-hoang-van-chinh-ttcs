import { useState, useEffect, useMemo, useRef } from 'react'
import { Link } from 'react-router-dom'
import Plus from 'lucide-react/dist/esm/icons/plus'
import Pencil from 'lucide-react/dist/esm/icons/pencil'
import Trash2 from 'lucide-react/dist/esm/icons/trash-2'
import ChevronLeft from 'lucide-react/dist/esm/icons/chevron-left'
import ChevronRight from 'lucide-react/dist/esm/icons/chevron-right'
import ChevronDown from 'lucide-react/dist/esm/icons/chevron-down'
import AdminTopbar from '../../components/admin/AdminTopbar.jsx'
import { fetchAdminPosts, deleteAdminPost } from '../../lib/fetchModelData.js'

const STATUS_OPTIONS = [
  { value: '', label: 'Tất cả trạng thái' },
  { value: 'PUBLISHED', label: 'Đã xuất bản' },
  { value: 'DRAFT', label: 'Bản nháp' },
]

const StatusSelect = ({ value, onChange }) => {
  const [open, setOpen] = useState(false)
  const ref = useRef(null)

  useEffect(() => {
    const handler = (e) => { if (ref.current && !ref.current.contains(e.target)) setOpen(false) }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [])

  const selected = STATUS_OPTIONS.find((o) => o.value === value) ?? STATUS_OPTIONS[0]

  return (
    <div ref={ref} className="relative">
      <button
        type="button"
        onClick={() => setOpen((o) => !o)}
        className="flex items-center gap-2 bg-surface-white border border-outline-variant/30 rounded-xl py-3 px-4 text-on-surface font-body-md text-body-md shadow-sm min-w-[180px] justify-between"
      >
        <span>{selected.label}</span>
        <ChevronDown size={16} className={`text-on-surface-variant transition-transform ${open ? 'rotate-180' : ''}`} />
      </button>

      {open ? (
        <div className="absolute top-full mt-1 left-0 min-w-full bg-surface-white border border-outline-variant/20 rounded-xl shadow-[0_8px_24px_rgba(0,0,0,0.1)] z-50 overflow-hidden py-1">
          {STATUS_OPTIONS.map((opt) => (
            <button
              key={opt.value}
              type="button"
              onClick={() => { onChange(opt.value); setOpen(false) }}
              className={`w-full text-left px-4 py-2.5 font-body-md text-body-md transition-colors hover:bg-secondary-container/20 hover:text-on-secondary-container ${
                value === opt.value ? 'bg-secondary-container/20 text-on-secondary-container font-semibold' : 'text-on-surface'
              }`}
            >
              {opt.label}
            </button>
          ))}
        </div>
      ) : null}
    </div>
  )
}

const StatusBadge = ({ status }) =>
  status === 'PUBLISHED' ? (
    <span className="inline-flex items-center px-2 py-1 rounded-full text-status-success bg-surface-white border border-status-success/30 font-label-sm text-label-sm">
      PUBLISHED
    </span>
  ) : (
    <span className="inline-flex items-center px-2 py-1 rounded-full text-status-draft bg-surface-white border border-status-draft/30 font-label-sm text-label-sm">
      DRAFT
    </span>
  )

const AdminPostsPage = () => {
  const [posts, setPosts] = useState([])
  const [pagination, setPagination] = useState({ currentPage: 1, totalPages: 1, totalItems: 0 })
  const [currentPage, setCurrentPage] = useState(1)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [deletingId, setDeletingId] = useState(null)
  const [statusFilter, setStatusFilter] = useState('')

  useEffect(() => {
    document.title = 'Quản lý bài viết | Admin - HVChinh Blog'
  }, [])

  useEffect(() => {
    fetchAdminPosts(currentPage)
      .then((data) => {
        setPosts(data.posts ?? [])
        setPagination(data.pagination ?? { currentPage: 1, totalPages: 1, totalItems: 0 })
      })
      .catch((err) => setError(err.message ?? 'Lỗi tải dữ liệu'))
      .finally(() => setLoading(false))
  }, [currentPage])

  const filtered = useMemo(
    () => posts.filter((p) => (statusFilter ? p.status === statusFilter : true)),
    [posts, statusFilter]
  )

  const visiblePages = useMemo(() => {
    const total = pagination.totalPages ?? 1
    const cur = pagination.currentPage ?? 1
    const start = Math.max(1, cur - 2)
    const end = Math.min(total, cur + 2)
    return Array.from({ length: end - start + 1 }, (_, i) => start + i)
  }, [pagination])

  const handleDelete = async (id) => {
    if (!window.confirm('Xác nhận xóa bài viết này?')) return
    setDeletingId(id)
    try {
      await deleteAdminPost(id)
      setPosts((prev) => prev.filter((p) => p.id !== id))
      setPagination((prev) => ({ ...prev, totalItems: Math.max(0, (prev.totalItems ?? 0) - 1) }))
    } catch (err) {
      alert('Xóa thất bại: ' + (err.message ?? 'Lỗi không xác định'))
    } finally {
      setDeletingId(null)
    }
  }

  const formatDate = (isoString) => {
    if (!isoString) return '—'
    return new Date(isoString).toLocaleDateString('vi-VN')
  }

  return (
    <>
      <AdminTopbar title="Quản lý bài viết" />

      <div className="flex-1 overflow-y-auto p-margin-mobile md:p-margin-desktop">
        {/* Actions & filters */}
        <div className="flex flex-wrap justify-between items-center gap-4 mb-8">
          <StatusSelect value={statusFilter} onChange={setStatusFilter} />
          <Link
            to="/admin/posts/new"
            className="bg-secondary-container text-on-secondary-fixed font-label-md text-label-md py-3 px-6 rounded-xl hover:opacity-90 transition-opacity shadow-sm whitespace-nowrap flex items-center gap-2"
          >
            <Plus size={18} />
            Thêm bài viết mới
          </Link>
        </div>

        {loading ? (
          <p className="text-on-surface-variant font-body-md">Đang tải...</p>
        ) : error ? (
          <p className="text-error font-body-md">{error}</p>
        ) : (
          <div className="bg-surface-white rounded-xl shadow-[0px_10px_25px_rgba(0,0,0,0.04)] overflow-hidden border border-outline-variant/10">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-outline-variant/20 bg-surface-container-low text-on-surface-variant font-label-md text-label-md uppercase tracking-wider">
                    <th className="p-4 font-bold">Tiêu đề</th>
                    <th className="p-4 font-bold">Trạng thái</th>
                    <th className="p-4 font-bold">Ngày cập nhật</th>
                    <th className="p-4 font-bold text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-outline-variant/10 font-body-md text-on-surface">
                  {filtered.map((post) => (
                    <tr key={post.id} className="hover:bg-surface-container-lowest transition-colors">
                      <td className="p-4 font-semibold text-on-background">{post.title}</td>
                      <td className="p-4">
                        <StatusBadge status={post.status} />
                      </td>
                      <td className="p-4 text-on-surface-variant">{formatDate(post.updatedAt)}</td>
                      <td className="p-4 text-right">
                        <Link
                          to={`/admin/posts/${post.id}/edit`}
                          className="text-on-surface-variant hover:text-secondary transition-colors p-2 inline-flex"
                          aria-label="Sửa"
                        >
                          <Pencil size={20} />
                        </Link>
                        <button
                          type="button"
                          aria-label="Xóa"
                          disabled={deletingId === post.id}
                          onClick={() => handleDelete(post.id)}
                          className="text-on-surface-variant hover:text-error transition-colors p-2 disabled:opacity-50"
                        >
                          <Trash2 size={20} />
                        </button>
                      </td>
                    </tr>
                  ))}
                  {filtered.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="p-8 text-center text-on-surface-variant font-body-md">
                        Không tìm thấy bài viết nào
                      </td>
                    </tr>
                  ) : null}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            <div className="border-t border-outline-variant/20 p-4 flex items-center justify-between bg-surface-white">
              <span className="text-on-surface-variant font-label-md text-label-md">
                Hiển thị {filtered.length} của {pagination.totalItems ?? posts.length} bài viết
              </span>
              <div className="flex gap-2">
                <button
                  type="button"
                  disabled={!pagination.hasPrevious}
                  onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                  className="p-2 border border-outline-variant/30 rounded-xl text-on-surface-variant hover:bg-surface-container-low transition-colors disabled:opacity-50"
                >
                  <ChevronLeft size={20} />
                </button>
                {visiblePages.map((page) => (
                  <button
                    key={page}
                    type="button"
                    onClick={() => setCurrentPage(page)}
                    className={`w-10 h-10 border rounded-xl font-label-md font-bold transition-colors ${
                      page === (pagination.currentPage ?? 1)
                        ? 'border-primary-container bg-primary-container text-on-primary-container'
                        : 'border-outline-variant/30 text-on-surface hover:bg-surface-container-low'
                    }`}
                  >
                    {page}
                  </button>
                ))}
                <button
                  type="button"
                  disabled={!pagination.hasNext}
                  onClick={() => setCurrentPage((p) => p + 1)}
                  className="p-2 border border-outline-variant/30 rounded-xl text-on-surface-variant hover:bg-surface-container-low transition-colors disabled:opacity-50"
                >
                  <ChevronRight size={20} />
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  )
}

export default AdminPostsPage

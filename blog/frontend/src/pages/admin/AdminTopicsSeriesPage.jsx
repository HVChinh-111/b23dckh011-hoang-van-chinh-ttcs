import { useState, useEffect } from 'react'
import Tag from 'lucide-react/dist/esm/icons/tag'
import BookOpen from 'lucide-react/dist/esm/icons/book-open'
import Plus from 'lucide-react/dist/esm/icons/plus'
import Pencil from 'lucide-react/dist/esm/icons/pencil'
import Trash2 from 'lucide-react/dist/esm/icons/trash-2'
import GripVertical from 'lucide-react/dist/esm/icons/grip-vertical'

import AdminTopbar from '../../components/admin/AdminTopbar.jsx'
import {
  fetchTopics,
  fetchSeriesList,
  fetchSeriesPosts,
  createTopic,
  updateTopic,
  deleteTopic,
  createSeries,
  updateSeries,
  deleteSeries,
} from '../../lib/fetchModelData.js'

const EMPTY_TOPIC = { name: '', slug: '', description: '' }
const EMPTY_SERIES = { name: '', slug: '', description: '' }

const AdminTopicsSeriesPage = () => {
  const [topics, setTopics] = useState([])
  const [seriesList, setSeriesList] = useState([])
  const [selectedSeries, setSelectedSeries] = useState(null)
  const [seriesItems, setSeriesItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // Topic form
  const [topicForm, setTopicForm] = useState(EMPTY_TOPIC)
  const [editingTopicId, setEditingTopicId] = useState(null)
  const [topicFormOpen, setTopicFormOpen] = useState(false)
  const [topicSaving, setTopicSaving] = useState(false)

  // Series form
  const [seriesForm, setSeriesForm] = useState(EMPTY_SERIES)
  const [editingSeriesId, setEditingSeriesId] = useState(null)
  const [seriesFormOpen, setSeriesFormOpen] = useState(false)
  const [seriesSaving, setSeriesSaving] = useState(false)

  // Drag state
  const [dragIdx, setDragIdx] = useState(null)

  useEffect(() => {
    document.title = 'Quản lý Phân loại | Admin - HVChinh Blog'
  }, [])

  useEffect(() => {
    Promise.all([fetchTopics(), fetchSeriesList()])
      .then(([t, s]) => { setTopics(t); setSeriesList(s) })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  // ── Topic handlers ─────────────────────────────────────────────────────────

  const openAddTopic = () => {
    setTopicForm(EMPTY_TOPIC)
    setEditingTopicId(null)
    setTopicFormOpen(true)
  }

  const openEditTopic = (topic) => {
    setTopicForm({ name: topic.name, slug: topic.slug, description: topic.description ?? '' })
    setEditingTopicId(topic.id)
    setTopicFormOpen(true)
  }

  const handleTopicSave = async () => {
    if (!topicForm.name.trim() || !topicForm.slug.trim()) return
    setTopicSaving(true)
    try {
      if (editingTopicId) {
        const updated = await updateTopic(editingTopicId, topicForm)
        setTopics((prev) => prev.map((t) => t.id === editingTopicId ? { ...t, ...updated } : t))
      } else {
        const created = await createTopic(topicForm)
        setTopics((prev) => [...prev, { ...created, publishedPostCount: 0 }])
      }
      setTopicFormOpen(false)
      setTopicForm(EMPTY_TOPIC)
      setEditingTopicId(null)
    } catch (err) {
      alert('Lưu thất bại: ' + (err.message ?? 'Lỗi không xác định'))
    } finally {
      setTopicSaving(false)
    }
  }

  const handleTopicDelete = async (id) => {
    if (!window.confirm('Xác nhận xóa topic này?')) return
    try {
      await deleteTopic(id)
      setTopics((prev) => prev.filter((t) => t.id !== id))
      if (selectedSeries && selectedSeries.id === id) setSelectedSeries(null)
    } catch (err) {
      alert('Xóa thất bại: ' + (err.message ?? 'Lỗi không xác định'))
    }
  }

  // ── Series handlers ────────────────────────────────────────────────────────

  const openAddSeries = () => {
    setSeriesForm(EMPTY_SERIES)
    setEditingSeriesId(null)
    setSeriesFormOpen(true)
  }

  const openEditSeries = (series) => {
    setSeriesForm({ name: series.name, slug: series.slug, description: series.description ?? '' })
    setEditingSeriesId(series.id)
    setSeriesFormOpen(true)
  }

  const handleSeriesSave = async () => {
    if (!seriesForm.name.trim() || !seriesForm.slug.trim()) return
    setSeriesSaving(true)
    try {
      if (editingSeriesId) {
        const updated = await updateSeries(editingSeriesId, seriesForm)
        setSeriesList((prev) => prev.map((s) => s.id === editingSeriesId ? { ...s, ...updated } : s))
      } else {
        const created = await createSeries(seriesForm)
        setSeriesList((prev) => [...prev, created])
      }
      setSeriesFormOpen(false)
      setSeriesForm(EMPTY_SERIES)
      setEditingSeriesId(null)
    } catch (err) {
      alert('Lưu thất bại: ' + (err.message ?? 'Lỗi không xác định'))
    } finally {
      setSeriesSaving(false)
    }
  }

  const handleSeriesDelete = async (id) => {
    if (!window.confirm('Xác nhận xóa series này?')) return
    try {
      await deleteSeries(id)
      setSeriesList((prev) => prev.filter((s) => s.id !== id))
      if (selectedSeries?.id === id) { setSelectedSeries(null); setSeriesItems([]) }
    } catch (err) {
      alert('Xóa thất bại: ' + (err.message ?? 'Lỗi không xác định'))
    }
  }

  const handleSelectSeries = async (series) => {
    if (selectedSeries?.id === series.id) {
      setSelectedSeries(null)
      setSeriesItems([])
      return
    }
    setSelectedSeries(series)
    try {
      const data = await fetchSeriesPosts(series.slug)
      setSeriesItems(data.items ?? [])
    } catch {
      setSeriesItems([])
    }
  }

  // ── Drag-and-drop reorder ──────────────────────────────────────────────────

  const handleDragStart = (idx) => setDragIdx(idx)

  const handleDragOver = (e, idx) => {
    e.preventDefault()
    if (dragIdx === null || dragIdx === idx) return
    setSeriesItems((prev) => {
      const next = [...prev]
      const [moved] = next.splice(dragIdx, 1)
      next.splice(idx, 0, moved)
      setDragIdx(idx)
      return next
    })
  }

  const handleDrop = async () => {
    setDragIdx(null)
    if (!selectedSeries) return
    const postOrders = seriesItems.map((item, i) => ({
      seriesPostItemId: item.seriesPostItemId,
      sequenceNumber: i + 1,
    }))
    try {
      await updateSeries(selectedSeries.id, {
        name: selectedSeries.name,
        slug: selectedSeries.slug,
        description: selectedSeries.description ?? '',
        postOrders,
      })
    } catch (err) {
      alert('Cập nhật thứ tự thất bại: ' + (err.message ?? ''))
    }
  }

  if (loading) return (
    <>
      <AdminTopbar title="Quản lý Phân loại" />
      <div className="flex-1 overflow-y-auto p-margin-mobile md:p-margin-desktop">
        <p className="text-on-surface-variant font-body-md">Đang tải...</p>
      </div>
    </>
  )

  return (
    <>
      <AdminTopbar title="Quản lý Phân loại" />

      <div className="flex-1 overflow-y-auto p-margin-mobile md:p-margin-desktop">
        {error ? <p className="text-error font-body-md mb-6">{error}</p> : null}

        <div className="grid grid-cols-1 xl:grid-cols-2 gap-gutter">

          {/* Topics section */}
          <section className="bg-surface-white rounded-xl shadow-[0_10px_25px_rgba(0,0,0,0.04)] p-6 flex flex-col border border-outline-variant/30">
            <div className="flex justify-between items-center mb-6">
              <h3 className="font-headline-md text-headline-md text-on-surface flex items-center gap-2">
                <Tag size={22} className="text-secondary-container" />
                Chủ đề (Topics)
              </h3>
              <button
                type="button"
                onClick={openAddTopic}
                className="bg-on-background text-surface-white font-label-md text-label-md px-4 py-2 rounded-xl hover:opacity-90 transition-opacity flex items-center gap-1 shadow-sm"
              >
                <Plus size={18} />
                Thêm Topic
              </button>
            </div>

            {/* Inline add/edit form */}
            {topicFormOpen ? (
              <div className="mb-4 p-4 bg-surface-container-low rounded-xl border border-outline-variant/30 flex flex-col gap-3">
                <input
                  type="text"
                  placeholder="Tên topic"
                  value={topicForm.name}
                  onChange={(e) => setTopicForm((f) => ({ ...f, name: e.target.value }))}
                  className="w-full bg-surface-white border border-outline-variant/30 rounded-lg px-3 py-2 font-body-md text-on-surface outline-none"
                />
                <input
                  type="text"
                  placeholder="slug (a-z0-9-)"
                  value={topicForm.slug}
                  onChange={(e) => setTopicForm((f) => ({ ...f, slug: e.target.value }))}
                  className="w-full bg-surface-white border border-outline-variant/30 rounded-lg px-3 py-2 font-body-md text-on-surface outline-none font-mono"
                />
                <textarea
                  rows={2}
                  placeholder="Mô tả (tuỳ chọn)"
                  value={topicForm.description}
                  onChange={(e) => setTopicForm((f) => ({ ...f, description: e.target.value }))}
                  className="w-full bg-surface-white border border-outline-variant/30 rounded-lg px-3 py-2 font-body-md text-on-surface outline-none resize-none"
                />
                <div className="flex gap-2 justify-end">
                  <button type="button" onClick={() => setTopicFormOpen(false)} className="bg-surface-white border border-outline text-on-surface font-label-md text-label-md py-2 px-4 rounded-xl hover:bg-surface-container-low transition-colors duration-200">
                    Hủy
                  </button>
                  <button type="button" disabled={topicSaving} onClick={handleTopicSave} className="bg-on-background text-surface-white font-label-md text-label-md py-2 px-4 rounded-xl hover:opacity-90 transition-opacity duration-200 shadow-md disabled:opacity-60 disabled:cursor-not-allowed">
                    {topicSaving ? 'Đang lưu...' : 'Lưu'}
                  </button>
                </div>
              </div>
            ) : null}

            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-outline-variant/50 text-on-surface-variant font-label-md text-label-md">
                    <th className="py-3 px-2 font-semibold">Tên</th>
                    <th className="py-3 px-2 font-semibold">Slug</th>
                    <th className="py-3 px-2 font-semibold text-center">Số bài</th>
                    <th className="py-3 px-2 font-semibold text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-outline-variant/30">
                  {topics.map((topic) => (
                    <tr key={topic.id} className="hover:bg-surface-container-lowest transition-colors">
                      <td className="py-4 px-2 font-medium text-on-surface font-body-md">{topic.name}</td>
                      <td className="py-4 px-2 text-on-surface-variant font-mono text-sm">/{topic.slug}</td>
                      <td className="py-4 px-2 text-center">
                        <span className="inline-flex items-center justify-center bg-surface-container-high text-on-surface px-2 py-1 rounded-full text-xs font-bold min-w-[2rem]">
                          {topic.publishedPostCount ?? 0}
                        </span>
                      </td>
                      <td className="py-4 px-2 text-right">
                        <button type="button" title="Sửa" onClick={() => openEditTopic(topic)} className="text-secondary hover:text-primary transition-colors p-1">
                          <Pencil size={18} />
                        </button>
                        <button type="button" title="Xóa" onClick={() => handleTopicDelete(topic.id)} className="text-error hover:opacity-70 transition-opacity p-1">
                          <Trash2 size={18} />
                        </button>
                      </td>
                    </tr>
                  ))}
                  {topics.length === 0 ? (
                    <tr><td colSpan={4} className="py-8 text-center text-on-surface-variant font-body-md">Chưa có topic nào</td></tr>
                  ) : null}
                </tbody>
              </table>
            </div>
          </section>

          {/* Series section */}
          <section className="bg-surface-white rounded-xl shadow-[0_10px_25px_rgba(0,0,0,0.04)] p-6 flex flex-col border border-outline-variant/30">
            <div className="flex justify-between items-center mb-6">
              <h3 className="font-headline-md text-headline-md text-on-surface flex items-center gap-2">
                <BookOpen size={22} className="text-status-success" />
                Series
              </h3>
              <button
                type="button"
                onClick={openAddSeries}
                className="bg-on-background text-surface-white font-label-md text-label-md px-4 py-2 rounded-xl hover:opacity-90 transition-opacity flex items-center gap-1 shadow-sm"
              >
                <Plus size={18} />
                Thêm Series
              </button>
            </div>

            {/* Inline add/edit form */}
            {seriesFormOpen ? (
              <div className="mb-4 p-4 bg-surface-container-low rounded-xl border border-outline-variant/30 flex flex-col gap-3">
                <input
                  type="text"
                  placeholder="Tên series"
                  value={seriesForm.name}
                  onChange={(e) => setSeriesForm((f) => ({ ...f, name: e.target.value }))}
                  className="w-full bg-surface-white border border-outline-variant/30 rounded-lg px-3 py-2 font-body-md text-on-surface outline-none"
                />
                <input
                  type="text"
                  placeholder="slug (a-z0-9-)"
                  value={seriesForm.slug}
                  onChange={(e) => setSeriesForm((f) => ({ ...f, slug: e.target.value }))}
                  className="w-full bg-surface-white border border-outline-variant/30 rounded-lg px-3 py-2 font-body-md text-on-surface outline-none font-mono"
                />
                <textarea
                  rows={2}
                  placeholder="Mô tả (tuỳ chọn)"
                  value={seriesForm.description}
                  onChange={(e) => setSeriesForm((f) => ({ ...f, description: e.target.value }))}
                  className="w-full bg-surface-white border border-outline-variant/30 rounded-lg px-3 py-2 font-body-md text-on-surface outline-none resize-none"
                />
                <div className="flex gap-2 justify-end">
                  <button type="button" onClick={() => setSeriesFormOpen(false)} className="bg-surface-white border border-outline text-on-surface font-label-md text-label-md py-2 px-4 rounded-xl hover:bg-surface-container-low transition-colors duration-200">
                    Hủy
                  </button>
                  <button type="button" disabled={seriesSaving} onClick={handleSeriesSave} className="bg-on-background text-surface-white font-label-md text-label-md py-2 px-4 rounded-xl hover:opacity-90 transition-opacity duration-200 shadow-md disabled:opacity-60 disabled:cursor-not-allowed">
                    {seriesSaving ? 'Đang lưu...' : 'Lưu'}
                  </button>
                </div>
              </div>
            ) : null}

            <div className="overflow-x-auto mb-6">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-outline-variant/50 text-on-surface-variant font-label-md text-label-md">
                    <th className="py-3 px-2 font-semibold">Tên</th>
                    <th className="py-3 px-2 font-semibold">Slug</th>
                    <th className="py-3 px-2 font-semibold text-center">Số bài</th>
                    <th className="py-3 px-2 font-semibold text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-outline-variant/30">
                  {seriesList.map((series) => (
                    <tr
                      key={series.id}
                      onClick={() => handleSelectSeries(series)}
                      className={`hover:bg-surface-container-lowest transition-colors cursor-pointer ${selectedSeries?.id === series.id ? 'bg-surface-container-low' : ''}`}
                    >
                      <td className={`py-4 px-2 font-medium text-on-surface font-body-md ${selectedSeries?.id === series.id ? 'font-bold border-l-4 border-secondary-container' : ''}`}>
                        {series.name}
                      </td>
                      <td className="py-4 px-2 text-on-surface-variant font-mono text-sm">/{series.slug}</td>
                      <td className="py-4 px-2 text-center">
                        <span className={`inline-flex items-center justify-center px-2 py-1 rounded-full text-xs font-bold min-w-[2rem] ${selectedSeries?.id === series.id ? 'bg-primary-container text-on-primary-container' : 'bg-surface-container-high text-on-surface'}`}>
                          {series.postCount ?? 0}
                        </span>
                      </td>
                      <td className="py-4 px-2 text-right" onClick={(e) => e.stopPropagation()}>
                        <button type="button" title="Sửa" onClick={() => openEditSeries(series)} className="text-secondary hover:text-primary transition-colors p-1">
                          <Pencil size={18} />
                        </button>
                        <button type="button" title="Xóa" onClick={() => handleSeriesDelete(series.id)} className="text-error hover:opacity-70 transition-opacity p-1">
                          <Trash2 size={18} />
                        </button>
                      </td>
                    </tr>
                  ))}
                  {seriesList.length === 0 ? (
                    <tr><td colSpan={4} className="py-8 text-center text-on-surface-variant font-body-md">Chưa có series nào</td></tr>
                  ) : null}
                </tbody>
              </table>
            </div>

            {/* Drag-drop ordering for selected series */}
            {selectedSeries ? (
              <div className="mt-auto border-t border-outline-variant/50 pt-6">
                <div className="flex items-center justify-between mb-4">
                  <h4 className="font-label-md text-label-md text-on-surface font-bold uppercase tracking-wider text-xs">
                    Sắp xếp: {selectedSeries.name}
                  </h4>
                  <span className="text-xs text-on-surface-variant flex items-center gap-1 bg-surface-container px-2 py-1 rounded">
                    <GripVertical size={14} />
                    Kéo thả để đổi vị trí
                  </span>
                </div>
                {seriesItems.length === 0 ? (
                  <p className="text-on-surface-variant font-body-md text-sm">Series chưa có bài viết nào.</p>
                ) : (
                  <div className="flex flex-col gap-2">
                    {seriesItems.map((item, idx) => (
                      <div
                        key={item.seriesPostItemId}
                        draggable
                        onDragStart={() => handleDragStart(idx)}
                        onDragOver={(e) => handleDragOver(e, idx)}
                        onDrop={handleDrop}
                        className="flex items-center gap-3 bg-surface border border-outline-variant/30 p-3 rounded-lg hover:border-secondary-container transition-colors group cursor-grab"
                      >
                        <GripVertical size={18} className="text-outline group-hover:text-secondary-container" />
                        <span className="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold shrink-0 bg-surface-container-high text-on-surface">
                          {idx + 1}
                        </span>
                        <span className="font-body-md text-sm text-on-surface truncate flex-1">{item.title}</span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ) : null}
          </section>
        </div>
      </div>
    </>
  )
}

export default AdminTopicsSeriesPage

import { useState, useEffect, useRef, useMemo } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { marked } from 'marked'
import Prism from 'prismjs'
import 'prismjs/themes/prism-okaidia.css'
import 'prismjs/components/prism-markup'
import 'prismjs/components/prism-java'
import 'prismjs/components/prism-javascript'
import 'prismjs/components/prism-jsx'
import 'prismjs/components/prism-typescript'
import 'prismjs/components/prism-sql'
import 'prismjs/components/prism-bash'
import 'prismjs/components/prism-json'
import 'prismjs/components/prism-yaml'
import 'prismjs/components/prism-css'

// Highlight code at parse time — no DOM manipulation needed
marked.use({
  renderer: {
    code({ text, lang }) {
      const language = lang && Prism.languages[lang] ? lang : null
      const highlighted = language
        ? Prism.highlight(text, Prism.languages[language], language)
        : text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      const cls = language ? ` class="language-${language}"` : ''
      return `<pre${cls}><code${cls}>${highlighted}</code></pre>\n`
    },
  },
})
import Menu from 'lucide-react/dist/esm/icons/menu'
import ChevronRight from 'lucide-react/dist/esm/icons/chevron-right'
import Save from 'lucide-react/dist/esm/icons/save'
import Upload from 'lucide-react/dist/esm/icons/upload'
import Pencil from 'lucide-react/dist/esm/icons/pencil'
import Bold from 'lucide-react/dist/esm/icons/bold'
import Italic from 'lucide-react/dist/esm/icons/italic'
import LinkIcon from 'lucide-react/dist/esm/icons/link'
import ImageIcon from 'lucide-react/dist/esm/icons/image'
import Code from 'lucide-react/dist/esm/icons/code'
import Eye from 'lucide-react/dist/esm/icons/eye'
import Copy from 'lucide-react/dist/esm/icons/copy'
import Check from 'lucide-react/dist/esm/icons/check'
import Trash2 from 'lucide-react/dist/esm/icons/trash-2'
import {
  fetchAdminPostFormOptions,
  fetchAdminPostById,
  createAdminPost,
  updateAdminPost,
  uploadPostImage,
  fetchPostImages,
  deletePostImage,
} from '../../lib/fetchModelData.js'

const TOOLBAR_ITEMS = [
  { Icon: Bold, title: 'Bold' },
  { Icon: Italic, title: 'Italic' },
  { Icon: LinkIcon, title: 'Link' },
  { Icon: ImageIcon, title: 'Image' },
  { Icon: Code, title: 'Code' },
]

const AdminAddEditPostPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = Boolean(id)

  const [title, setTitle] = useState('')
  const [slug, setSlug] = useState('')
  const [content, setContent] = useState('')
  const [isPublished, setIsPublished] = useState(false)
  const [selectedTopicIds, setSelectedTopicIds] = useState([])
  const [selectedSeriesId, setSelectedSeriesId] = useState('')

  const [topicOptions, setTopicOptions] = useState([])
  const [seriesOptions, setSeriesOptions] = useState([])

  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)

  const [uploadedImages, setUploadedImages] = useState([])
  const [copiedId, setCopiedId] = useState(null)
  const imageInputRef = useRef(null)
  const previewRef = useRef(null)

  const handleImageUpload = async (e) => {
    const files = Array.from(e.target.files ?? [])
    e.target.value = ''
    for (const file of files) {
      try {
        const result = await uploadPostImage(file, isEdit ? id : null)
        setUploadedImages((prev) => [...prev, {
          id: result.id,
          name: result.fileName,
          previewUrl: result.url,
          markdownUrl: result.url,
        }])
      } catch (err) {
        alert('Upload thất bại: ' + (err.message ?? 'Lỗi không xác định'))
      }
    }
  }

  const handleDeleteImage = async (imgId) => {
    try {
      await deletePostImage(imgId)
      setUploadedImages((prev) => prev.filter((img) => img.id !== imgId))
    } catch (err) {
      alert('Xóa ảnh thất bại: ' + (err.message ?? 'Lỗi không xác định'))
    }
  }

  const handleCopyUrl = (id, url) => {
    navigator.clipboard.writeText(url).then(() => {
      setCopiedId(id)
      setTimeout(() => setCopiedId(null), 2000)
    })
  }

  const previewHtml = useMemo(() => {
    if (!content.trim()) return ''
    return marked(content, { gfm: true, breaks: false })
  }, [content])

  useEffect(() => {
    if (!previewRef.current) return
    const ICON_COPY = `<svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="14" height="14" x="8" y="8" rx="2" ry="2"/><path d="M4 16c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2h10c1.1 0 2 .9 2 2"/></svg>`
    const ICON_CHECK = `<svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>`
    previewRef.current.querySelectorAll('pre').forEach((pre) => {
      const code = pre.querySelector('code')
      if (!code || pre.querySelector('.copy-code-btn')) return
      const btn = document.createElement('button')
      btn.className = 'copy-code-btn'
      btn.innerHTML = ICON_COPY
      btn.title = 'Copy code'
      btn.addEventListener('click', () => {
        navigator.clipboard.writeText(code.innerText ?? '').then(() => {
          btn.innerHTML = ICON_CHECK
          btn.classList.add('copied')
          setTimeout(() => { btn.innerHTML = ICON_COPY; btn.classList.remove('copied') }, 2000)
        })
      })
      pre.appendChild(btn)
    })
  }, [previewHtml])

  useEffect(() => {
    document.title = `${isEdit ? 'Sửa bài viết' : 'Thêm bài viết'} | Admin - HVChinh Blog`
  }, [isEdit])

  useEffect(() => {
    const optionsPromise = fetchAdminPostFormOptions()
    const postPromise = isEdit ? fetchAdminPostById(id) : Promise.resolve(null)
    const imagesPromise = isEdit ? fetchPostImages(id) : Promise.resolve([])

    Promise.all([optionsPromise, postPromise, imagesPromise])
      .then(([options, postData, images]) => {
        setTopicOptions(options.topics ?? [])
        setSeriesOptions(options.seriesList ?? [])
        if (postData) {
          setTitle(postData.title ?? '')
          setSlug(postData.slug ?? '')
          setContent(postData.contentMarkdown ?? '')
          setIsPublished(postData.status === 'PUBLISHED')
          setSelectedTopicIds(postData.selectedTopicIds ?? [])
          setSelectedSeriesId(postData.selectedSeriesId ?? '')
        }
        setUploadedImages((images ?? []).map((img) => ({
          id: img.id,
          name: img.publicPath.split('/').pop(),
          previewUrl: img.publicPath,
          markdownUrl: img.publicPath,
        })))
      })
      .catch((err) => setError(err.message ?? 'Lỗi tải dữ liệu'))
      .finally(() => setLoading(false))
  }, [id, isEdit])

  const handleSave = async () => {
    if (!title.trim() || !slug.trim() || !content.trim()) {
      setError('Tiêu đề, slug và nội dung không được để trống')
      return
    }
    setSaving(true)
    setError(null)
    const body = {
      title,
      slug,
      contentMarkdown: content,
      status: isPublished ? 'PUBLISHED' : 'DRAFT',
      topicIds: selectedTopicIds,
      seriesId: selectedSeriesId || null,
    }
    try {
      if (isEdit) {
        await updateAdminPost(id, body)
      } else {
        await createAdminPost(body)
      }
      navigate('/admin/posts')
    } catch (err) {
      setError(err.message ?? 'Lưu thất bại')
    } finally {
      setSaving(false)
    }
  }

  const handleTopicsChange = (e) => {
    setSelectedTopicIds(Array.from(e.target.selectedOptions, (o) => o.value))
  }

  return (
    <>
      {/* Custom header */}
      <header className="bg-surface-white/80 backdrop-blur-xl border-b border-on-background/5 shadow-sm h-16 flex-shrink-0 z-10 px-margin-mobile md:px-margin-desktop flex justify-between items-center">
        <div className="flex items-center gap-4">
          <button type="button" className="md:hidden text-on-surface p-2 rounded-xl hover:bg-surface-container">
            <Menu size={20} />
          </button>
          <div className="flex items-center gap-2">
            <Link
              to="/admin/posts"
              className="text-on-surface-variant hover:text-on-background transition-colors font-label-md text-label-md"
            >
              Bài viết
            </Link>
            <ChevronRight size={16} className="text-outline" />
            <span className="font-bold text-on-background font-label-md text-label-md">
              {isEdit ? 'Sửa bài viết' : 'Thêm mới'}
            </span>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <button
            type="button"
            onClick={() => navigate('/admin/posts')}
            className="px-6 py-2 border border-outline-variant text-on-surface font-label-md text-label-md rounded-xl hover:bg-surface-container transition-all"
          >
            Hủy
          </button>
          <button
            type="button"
            disabled={saving || loading}
            onClick={handleSave}
            className="px-6 py-2 bg-on-background text-surface-white font-label-md text-label-md rounded-xl hover:opacity-90 transition-all flex items-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed"
          >
            <Save size={16} />
            {saving ? 'Đang lưu...' : 'Lưu bài viết'}
          </button>
        </div>
      </header>

      {/* Content */}
      <div className="flex-1 overflow-y-auto p-margin-mobile md:p-gutter">
        {loading ? (
          <p className="text-on-surface-variant font-body-md">Đang tải...</p>
        ) : (
          <div className="max-w-container-max mx-auto flex flex-col gap-gutter">

            {error ? (
              <div className="bg-error-container text-on-error-container font-label-md text-label-md p-4 rounded-xl border border-error/20">
                {error}
              </div>
            ) : null}

            {/* Meta settings card */}
            <div className="bg-surface-white rounded-xl shadow-[0_10px_25px_rgba(0,0,0,0.04)] p-6 border border-outline-variant/20">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Left col */}
                <div className="flex flex-col gap-4">
                  <div>
                    <label className="block font-label-md text-label-md text-on-surface mb-2">Tiêu đề</label>
                    <input
                      type="text"
                      placeholder="Nhập tiêu đề bài viết..."
                      value={title}
                      onChange={(e) => setTitle(e.target.value)}
                      className="w-full bg-surface-white border border-outline-variant/30 rounded-xl px-4 py-3 font-body-md text-on-surface focus:border-on-background focus:ring-1 focus:ring-on-background outline-none transition-all placeholder:text-outline"
                    />
                  </div>
                  <div>
                    <label className="block font-label-md text-label-md text-on-surface mb-2">Slug</label>
                    <input
                      type="text"
                      placeholder="nhap-tieu-de-bai-viet"
                      value={slug}
                      onChange={(e) => setSlug(e.target.value)}
                      className="w-full bg-surface-white border border-outline-variant/30 rounded-xl px-4 py-3 font-body-md text-on-surface focus:border-on-background focus:ring-1 focus:ring-on-background outline-none transition-all placeholder:text-outline"
                    />
                  </div>

                </div>

                {/* Right col */}
                <div className="flex flex-col gap-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block font-label-md text-label-md text-on-surface mb-2">Chủ đề</label>
                      <select
                        multiple
                        value={selectedTopicIds}
                        onChange={handleTopicsChange}
                        className="w-full bg-surface-white border border-outline-variant/30 rounded-xl px-4 py-2 font-body-md text-on-surface focus:border-on-background focus:ring-1 focus:ring-on-background outline-none h-[104px] transition-all"
                      >
                        {topicOptions.map((t) => (
                          <option key={t.id} value={t.id}>{t.name}</option>
                        ))}
                      </select>
                    </div>
                    <div className="flex flex-col gap-3">
                      <div>
                        <label className="block font-label-md text-label-md text-on-surface mb-2">Series</label>
                        <select
                          value={selectedSeriesId}
                          onChange={(e) => setSelectedSeriesId(e.target.value)}
                          className="w-full bg-surface-white border border-outline-variant/30 rounded-xl px-4 py-3 font-body-md text-on-surface focus:border-on-background focus:ring-1 focus:ring-on-background outline-none transition-all"
                        >
                          <option value="">-- Chọn Series --</option>
                          {seriesOptions.map((s) => (
                            <option key={s.id} value={s.id}>{s.name}</option>
                          ))}
                        </select>
                      </div>
                      <div className="flex items-center justify-between p-3 bg-surface-container-low rounded-xl border border-outline-variant/20">
                        <span className="font-label-md text-label-md text-on-surface">Trạng thái</span>
                        <label className="relative inline-flex items-center cursor-pointer">
                          <input
                            type="checkbox"
                            className="sr-only peer"
                            checked={isPublished}
                            onChange={() => setIsPublished((prev) => !prev)}
                          />
                          <div className="w-11 h-6 bg-surface-variant rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border after:border-gray-300 after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-status-success" />
                          <span className={`ml-3 font-label-sm text-label-sm ${isPublished ? 'text-status-success' : 'text-on-surface-variant'}`}>
                            {isPublished ? 'Published' : 'Draft'}
                          </span>
                        </label>
                      </div>
                    </div>
                  </div>

                  {/* Image library */}
                  <div className="flex flex-col gap-2">
                    <div className="flex items-center justify-between">
                      <label className="font-label-md text-label-md text-on-surface">Thư viện ảnh</label>
                      <button
                        type="button"
                        onClick={() => imageInputRef.current?.click()}
                        className="flex items-center gap-1.5 px-3 py-1.5 bg-on-background text-surface-white font-label-sm text-label-sm rounded-lg hover:opacity-90 transition-opacity"
                      >
                        <Upload size={13} />
                        Tải ảnh lên
                      </button>
                      <input ref={imageInputRef} type="file" accept="image/*" multiple className="hidden" onChange={handleImageUpload} />
                    </div>

                    <div className="border border-outline-variant/30 rounded-xl overflow-hidden bg-surface-container-low min-h-[80px] max-h-[180px] overflow-y-auto">
                      {uploadedImages.length === 0 ? (
                        <div className="flex flex-col items-center justify-center h-20 text-on-surface-variant">
                          <ImageIcon size={20} className="mb-1 opacity-40" />
                          <span className="font-body-md text-xs opacity-60">Chưa có ảnh nào</span>
                        </div>
                      ) : (
                        <div className="divide-y divide-outline-variant/20">
                          {uploadedImages.map((img) => (
                            <div key={img.id} className="flex items-center gap-2 px-3 py-2">
                              <img src={img.previewUrl} alt={img.name} className="w-9 h-9 rounded-lg object-cover shrink-0 border border-outline-variant/20" />
                              <div className="flex-1 min-w-0">
                                <p className="font-label-sm text-label-sm text-on-surface truncate">{img.name}</p>
                                <p className="font-mono text-xs text-on-surface-variant truncate">{img.markdownUrl}</p>
                              </div>
                              <button
                                type="button"
                                title="Copy URL"
                                onClick={() => handleCopyUrl(img.id, img.markdownUrl)}
                                className={`p-1.5 rounded-lg transition-colors shrink-0 ${copiedId === img.id ? 'text-status-success' : 'text-on-surface-variant hover:text-on-background hover:bg-surface-container'}`}
                              >
                                {copiedId === img.id ? <Check size={14} /> : <Copy size={14} />}
                              </button>
                              <button
                                type="button"
                                title="Xóa"
                                onClick={() => handleDeleteImage(img.id)}
                                className="p-1.5 rounded-lg text-on-surface-variant hover:text-error hover:bg-error/5 transition-colors shrink-0"
                              >
                                <Trash2 size={14} />
                              </button>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Editor split view */}
            <div className="flex gap-6 min-h-[500px] pb-8">
              {/* Markdown editor */}
              <div className="flex-1 bg-surface-white rounded-xl shadow-[0_10px_25px_rgba(0,0,0,0.04)] flex flex-col border border-outline-variant/20 overflow-hidden">
                <div className="bg-surface-container-low px-4 py-3 border-b border-outline-variant/20 flex justify-between items-center flex-shrink-0">
                  <span className="font-label-md text-label-md text-on-surface flex items-center gap-2">
                    <Pencil size={16} />
                    Markdown Editor
                  </span>
                  <div className="flex gap-1">
                    {TOOLBAR_ITEMS.map(({ Icon, title: t }) => (
                      <button
                        key={t}
                        type="button"
                        title={t}
                        className="p-1.5 text-on-surface-variant hover:text-on-background hover:bg-surface-container rounded transition-colors"
                      >
                        <Icon size={16} />
                      </button>
                    ))}
                  </div>
                </div>
                <textarea
                  className="flex-1 w-full p-6 bg-transparent font-mono text-body-md text-on-surface leading-relaxed resize-none outline-none"
                  placeholder="Bắt đầu viết nội dung bằng Markdown..."
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                />
              </div>

              {/* Live preview (static display) */}
              <div className="flex-1 bg-surface-white rounded-xl shadow-[0_10px_25px_rgba(0,0,0,0.04)] flex flex-col border border-outline-variant/20 overflow-hidden">
                <div className="bg-surface-container-low px-4 py-3 border-b border-outline-variant/20 flex items-center gap-2 flex-shrink-0">
                  <Eye size={16} className="text-on-surface" />
                  <span className="font-label-md text-label-md text-on-surface">Preview</span>
                </div>
                <div
                  ref={previewRef}
                  className="flex-1 p-8 overflow-y-auto prose font-body-md text-body-md text-on-surface-variant space-y-4"
                  style={{ backgroundColor: '#fafafa' }}
                  dangerouslySetInnerHTML={{ __html: previewHtml }}
                />
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  )
}

export default AdminAddEditPostPage

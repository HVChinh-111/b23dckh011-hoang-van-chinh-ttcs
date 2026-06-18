import { useState, useEffect, useMemo } from 'react'
import { useParams } from 'react-router-dom'
import FolderOpen from 'lucide-react/dist/esm/icons/folder-open'
import BookOpen from 'lucide-react/dist/esm/icons/book-open'
import Header from '../components/layout/Header.jsx'
import Footer from '../components/layout/Footer.jsx'
import PostList from '../components/home/PostList.jsx'
import Pagination from '../components/home/Pagination.jsx'
import Sidebar from '../components/sidebar/Sidebar.jsx'
import {
  fetchProfile,
  fetchTopics,
  fetchSeriesList,
  fetchTopicPosts,
  fetchSeriesPosts,
} from '../lib/fetchModelData.js'

const SORT_OPTIONS = [
  { value: 'newest', label: 'Mới nhất' },
  { value: 'oldest', label: 'Cũ nhất' },
]

const PostListPage = ({ type }) => {
  const { slug } = useParams()
  const [sort, setSort] = useState('newest')
  const [pageData, setPageData] = useState(null)
  const [author, setAuthor] = useState(null)
  const [allTopics, setAllTopics] = useState([])
  const [allSeries, setAllSeries] = useState([])
  // pageMap tracks current page per context (type:slug) so we avoid a reset effect
  const [pageMap, setPageMap] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const contextKey = `${type}:${slug}`
  const currentPage = pageMap[contextKey] ?? 1

  const setCurrentPage = (page) =>
    setPageMap((prev) => ({ ...prev, [contextKey]: page }))

  useEffect(() => {
    const postsPromise = type === 'topic'
      ? fetchTopicPosts(slug, currentPage)
      : fetchSeriesPosts(slug, currentPage)

    Promise.all([postsPromise, fetchProfile(), fetchTopics(), fetchSeriesList()])
      .then(([postsData, profileData, topicsData, seriesData]) => {
        setPageData(postsData)
        setAuthor(profileData)
        setAllTopics(topicsData)
        setAllSeries(seriesData)
        setError(null)
      })
      .catch((err) => setError(err.message ?? 'Lỗi tải dữ liệu'))
      .finally(() => setLoading(false))
  }, [slug, type, currentPage])

  const meta = useMemo(() => {
    if (!pageData) return { name: slug, description: '' }
    const info = type === 'topic' ? pageData.topicInfo : pageData.seriesInfo
    return { name: info?.name ?? slug, description: info?.description ?? '' }
  }, [pageData, type, slug])

  const posts = useMemo(() => {
    if (!pageData) return []
    if (type === 'topic') return pageData.posts ?? []
    return (pageData.items ?? []).map((item) => ({
      id: item.postId,
      title: item.title,
      slug: item.slug,
      excerpt: item.summary ?? '',
      date: '',
      topics: [],
      imageUrl: null,
      imageAlt: item.title,
      status: null,
    }))
  }, [pageData, type])

  const sortedPosts = useMemo(() => {
    if (type === 'series') return posts
    return sort === 'oldest' ? [...posts].reverse() : posts
  }, [posts, sort, type])

  const paginationData = useMemo(
    () => pageData?.pagination ?? { currentPage: 1, totalPages: 1, totalItems: 0 },
    [pageData]
  )

  useEffect(() => {
    const prefix = type === 'topic' ? 'Chủ đề' : 'Series'
    document.title = `${prefix}: ${meta.name} | HVChinh Blog`
  }, [meta.name, type])

  const Icon = type === 'topic' ? FolderOpen : BookOpen
  const typeLabel = type === 'topic' ? 'Chủ đề' : 'Series'

  return (
    <div className="bg-primary-container text-on-background font-body-md antialiased min-h-screen flex flex-col">
      <Header />

      <main className="flex-grow w-full px-margin-mobile md:px-margin-desktop max-w-container-max mx-auto py-12 md:py-16">

        <div className="grid grid-cols-1 md:grid-cols-12 gap-gutter">

          <div className="md:col-span-8 flex flex-col gap-8">
            <div>
              <div className="flex items-center gap-2 mb-4">
                <Icon size={20} className="text-on-background" />
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  {typeLabel}
                </span>
              </div>
              <h1 className="font-display-lg text-display-lg mb-4">{meta.name}</h1>
              {meta.description ? (
                <p className="font-body-lg text-body-lg text-on-surface-variant">
                  {meta.description}
                </p>
              ) : null}
            </div>

            {loading ? (
              <p className="text-on-surface-variant font-body-md">Đang tải...</p>
            ) : error ? (
              <p className="text-error font-body-md">{error}</p>
            ) : (
              <>
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 border-b border-on-background/10 pb-4">
                  <p className="text-on-surface-variant font-body-md">
                    Tìm thấy <strong className="text-on-background">{paginationData.totalItems ?? posts.length}</strong> bài viết
                  </p>
                  {type !== 'series' ? (
                    <div className="flex items-center gap-3">
                      <label className="font-label-md text-label-md text-on-surface-variant">
                        Sắp xếp theo:
                      </label>
                      <select
                        value={sort}
                        onChange={(e) => setSort(e.target.value)}
                        className="bg-surface-white border border-outline-variant rounded-lg px-3 py-2 font-label-md text-label-md text-on-background focus:outline-none focus:border-primary"
                      >
                        {SORT_OPTIONS.map(({ value, label }) => (
                          <option key={value} value={value}>
                            {label}
                          </option>
                        ))}
                      </select>
                    </div>
                  ) : null}
                </div>

                <PostList posts={sortedPosts} author={author} />

                <Pagination
                  currentPage={paginationData.currentPage ?? 1}
                  totalPages={paginationData.totalPages ?? 1}
                  onPageChange={setCurrentPage}
                />
              </>
            )}
          </div>

          <Sidebar author={author ?? {}} topics={allTopics} series={allSeries} />
        </div>
      </main>

      <Footer author={author} />
    </div>
  )
}

export default PostListPage

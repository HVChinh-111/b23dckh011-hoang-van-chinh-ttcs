import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import Header from '../components/layout/Header.jsx'
import Footer from '../components/layout/Footer.jsx'
import PostList from '../components/home/PostList.jsx'
import Pagination from '../components/home/Pagination.jsx'
import Sidebar from '../components/sidebar/Sidebar.jsx'
import { searchPosts, fetchProfile, fetchTopics, fetchSeriesList } from '../lib/fetchModelData.js'

const SearchPage = () => {
  const [searchParams] = useSearchParams()
  const keyword = searchParams.get('q') ?? ''

  const [posts, setPosts] = useState([])
  const [pagination, setPagination] = useState({ currentPage: 1, totalPages: 1 })
  const [totalResults, setTotalResults] = useState(0)
  const [author, setAuthor] = useState(null)
  const [topics, setTopics] = useState([])
  const [series, setSeries] = useState([])
  const [currentPage, setCurrentPage] = useState(1)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    setCurrentPage(1)
  }, [keyword])

  useEffect(() => {
    document.title = keyword ? `Tìm kiếm: "${keyword}" | HVChinh Blog` : 'Tìm kiếm | HVChinh Blog'
  }, [keyword])

  useEffect(() => {
    if (!keyword) return
    setLoading(true)
    Promise.all([
      searchPosts(keyword, currentPage),
      fetchProfile(),
      fetchTopics(),
      fetchSeriesList(),
    ])
      .then(([searchData, profileData, topicsData, seriesData]) => {
        setPosts(searchData.posts)
        setPagination(searchData.pagination)
        setTotalResults(searchData.totalResults ?? searchData.posts.length)
        setAuthor(profileData)
        setTopics(topicsData)
        setSeries(seriesData)
        setError(null)
      })
      .catch((err) => setError(err.message ?? 'Lỗi tìm kiếm'))
      .finally(() => setLoading(false))
  }, [keyword, currentPage])

  return (
    <div className="bg-primary-container text-on-background font-body-md antialiased min-h-screen flex flex-col">
      <Header />
      <main className="flex-grow w-full px-margin-mobile md:px-margin-desktop max-w-container-max mx-auto py-12">
        <div className="grid grid-cols-1 md:grid-cols-12 gap-gutter">
          <div className="md:col-span-8 flex flex-col gap-8">
            <div>
              <p className="font-body-md text-body-md text-on-surface-variant">
                {keyword ? (
                  <>
                    Kết quả tìm kiếm cho{' '}
                    <strong className="text-on-background">"{keyword}"</strong>
                    {!loading && (
                      <> — <strong className="text-on-background">{totalResults}</strong> bài viết</>
                    )}
                  </>
                ) : 'Vui lòng nhập từ khóa để tìm kiếm.'}
              </p>
            </div>

            {loading ? (
              <p className="text-on-surface-variant font-body-md">Đang tìm kiếm...</p>
            ) : error ? (
              <p className="text-error font-body-md">{error}</p>
            ) : posts.length === 0 && keyword ? (
              <p className="text-on-surface-variant font-body-md">
                Không tìm thấy bài viết nào phù hợp.
              </p>
            ) : (
              <>
                <PostList posts={posts} author={author} />
                <Pagination
                  currentPage={pagination.currentPage ?? 1}
                  totalPages={pagination.totalPages ?? 1}
                  onPageChange={setCurrentPage}
                />
              </>
            )}
          </div>
          <Sidebar author={author ?? {}} topics={topics} series={series} />
        </div>
      </main>
      <Footer author={author} />
    </div>
  )
}

export default SearchPage

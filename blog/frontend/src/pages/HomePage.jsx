import { useState, useEffect } from 'react'
import Header from '../components/layout/Header.jsx'
import Footer from '../components/layout/Footer.jsx'
import PostList from '../components/home/PostList.jsx'
import Pagination from '../components/home/Pagination.jsx'
import Sidebar from '../components/sidebar/Sidebar.jsx'
import { fetchPosts, fetchProfile, fetchTopics, fetchSeriesList } from '../lib/fetchModelData.js'

const HomePage = () => {
  const [posts, setPosts] = useState([])
  const [pagination, setPagination] = useState({ currentPage: 1, totalPages: 1 })
  const [author, setAuthor] = useState(null)
  const [topics, setTopics] = useState([])
  const [series, setSeries] = useState([])
  const [currentPage, setCurrentPage] = useState(1)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    document.title = 'HVChinh Blog'
  }, [])

  useEffect(() => {
    Promise.all([
      fetchPosts(currentPage),
      fetchProfile(),
      fetchTopics(),
      fetchSeriesList(),
    ])
      .then(([postsData, profileData, topicsData, seriesData]) => {
        setPosts(postsData.posts)
        setPagination(postsData.pagination)
        setAuthor(profileData)
        setTopics(topicsData)
        setSeries(seriesData)
        setError(null)
      })
      .catch((err) => setError(err.message ?? 'Lỗi tải dữ liệu'))
      .finally(() => setLoading(false))
  }, [currentPage])

  return (
    <div className="bg-primary-container text-on-background font-body-md antialiased min-h-screen flex flex-col">
      <Header />
      <main className="flex-grow w-full px-margin-mobile md:px-margin-desktop max-w-container-max mx-auto py-12">
        <div className="grid grid-cols-1 md:grid-cols-12 gap-gutter">
          <div className="md:col-span-8 flex flex-col gap-8">
            {loading ? (
              <p className="text-on-surface-variant font-body-md">Đang tải...</p>
            ) : error ? (
              <p className="text-error font-body-md">{error}</p>
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

export default HomePage

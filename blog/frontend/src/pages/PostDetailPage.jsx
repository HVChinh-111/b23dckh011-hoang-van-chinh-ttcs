import { useState, useEffect, useLayoutEffect, useRef } from 'react'
import { Link, useParams } from 'react-router-dom'
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
import Clock from 'lucide-react/dist/esm/icons/clock'
import Header from '../components/layout/Header.jsx'
import Footer from '../components/layout/Footer.jsx'
import TableOfContents from '../components/post-detail/TableOfContents.jsx'
import SeriesNav from '../components/post-detail/SeriesNav.jsx'
import { fetchPostBySlug, fetchProfile } from '../lib/fetchModelData.js'

const PostDetailPage = () => {
  const { slug } = useParams()
  const [post, setPost] = useState(null)
  const [author, setAuthor] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const contentRef = useRef(null)

  useLayoutEffect(() => {
    if (!post) return
    document.title = `${post.title} | HVChinh Blog`
    if (!contentRef.current) return
    contentRef.current.querySelectorAll('pre').forEach((pre) => {
      const code = pre.querySelector('code')
      if (!code) return
      Prism.highlightElement(code)

      if (pre.querySelector('.copy-code-btn')) return
      const ICON_COPY = `<svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="14" height="14" x="8" y="8" rx="2" ry="2"/><path d="M4 16c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2h10c1.1 0 2 .9 2 2"/></svg>`
      const ICON_CHECK = `<svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>`
      const btn = document.createElement('button')
      btn.className = 'copy-code-btn'
      btn.innerHTML = ICON_COPY
      btn.title = 'Copy code'
      btn.addEventListener('click', () => {
        navigator.clipboard.writeText(code.innerText ?? '').then(() => {
          btn.innerHTML = ICON_CHECK
          btn.classList.add('copied')
          setTimeout(() => {
            btn.innerHTML = ICON_COPY
            btn.classList.remove('copied')
          }, 2000)
        })
      })
      pre.appendChild(btn)
    })
  }, [post])

  useEffect(() => {
    Promise.all([fetchPostBySlug(slug), fetchProfile()])
      .then(([postData, profileData]) => {
        setPost(postData)
        setAuthor(profileData)
        setError(null)
      })
      .catch((err) => setError(err.message ?? 'Không tìm thấy bài viết'))
      .finally(() => setLoading(false))
  }, [slug])

  return (
    <div className="bg-primary-container text-on-background min-h-screen flex flex-col font-body-md">
      <Header />

      <main className="flex-grow py-8 md:py-16">
        <div className="max-w-container-max mx-auto px-margin-mobile md:px-margin-desktop grid grid-cols-1 lg:grid-cols-12 gap-gutter">

          {loading ? (
            <div className="lg:col-span-12">
              <p className="text-on-surface-variant font-body-md">Đang tải...</p>
            </div>
          ) : error ? (
            <div className="lg:col-span-12">
              <p className="text-error font-body-md">{error}</p>
            </div>
          ) : post ? (
            <>
              {/* Article */}
              <article className="lg:col-span-9 bg-surface-white rounded-xl shadow-[0_10px_25px_rgba(0,0,0,0.04)] p-6 md:p-10">

                {/* Post header */}
                <div className="mb-8 border-b border-outline-variant/30 pb-8 flex flex-col gap-3">
                  {post.topics?.length > 0 ? (
                    <div className="flex flex-wrap gap-2">
                      {post.topics.map((topic) => (
                        <Link
                          key={topic.slug}
                          to={`/topics/${topic.slug}`}
                          className="inline-flex items-center px-3 py-1 rounded-full bg-secondary-container/20 text-on-secondary-container font-label-sm text-label-sm hover:bg-secondary-container/30 transition-colors"
                        >
                          {topic.name}
                        </Link>
                      ))}
                    </div>
                  ) : null}

                  <h1 className="font-headline-lg-mobile md:font-headline-lg text-headline-lg-mobile md:text-headline-lg text-on-background">
                    {post.title}
                  </h1>

                  {author ? (
                    <div className="flex items-center gap-2 text-on-surface-variant font-label-sm text-label-sm">
                      {author.avatarUrl ? (
                        <img
                          src={author.avatarUrl}
                          alt={author.name}
                          className="w-6 h-6 rounded-full object-cover shrink-0"
                        />
                      ) : null}
                      <span>By {author.name}</span>
                      <span className="w-px h-4 bg-outline-variant shrink-0" />
                      <Clock size={14} className="shrink-0" />
                      <span>
                        {post.publishedAt
                          ? new Date(post.publishedAt).toLocaleDateString('vi-VN', {
                              day: 'numeric',
                              month: 'long',
                              year: 'numeric',
                            })
                          : ''}
                      </span>
                    </div>
                  ) : null}
                </div>

                {/* Post body — rendered HTML from backend */}
                <div
                  ref={contentRef}
                  className="prose font-body-md text-body-md text-on-surface-variant space-y-4"
                  dangerouslySetInnerHTML={{ __html: post.contentHtml }}
                />
              </article>

              {/* Right sticky panel */}
              <aside className="hidden lg:block lg:col-span-3">
                <div className="sticky top-24 flex flex-col gap-6">
                  {post.toc?.length > 0 ? <TableOfContents items={post.toc} /> : null}
                  {post.series ? <SeriesNav series={post.series} /> : null}
                </div>
              </aside>
            </>
          ) : null}

        </div>
      </main>

      <Footer author={author} />
    </div>
  )
}

export default PostDetailPage

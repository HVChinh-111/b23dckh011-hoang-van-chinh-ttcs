import { useState, useEffect, useMemo } from 'react'
import { Link, useLocation } from 'react-router-dom'
import ChevronDown from 'lucide-react/dist/esm/icons/chevron-down'
import { fetchTopics, fetchSeriesList } from '../../lib/fetchModelData.js'

const Header = () => {
  const { pathname } = useLocation()
  const [topics, setTopics] = useState([])
  const [series, setSeries] = useState([])

  useEffect(() => {
    fetchTopics().then(setTopics).catch(() => {})
    fetchSeriesList().then(setSeries).catch(() => {})
  }, [])

  const navItems = useMemo(() => [
    {
      label: 'Bài viết',
      to: '/',
      isActive: (path) => path === '/' || path.startsWith('/posts'),
      dropdown: null,
    },
    {
      label: 'Chủ đề',
      to: '/',
      isActive: (path) => path.startsWith('/topics'),
      dropdown: { basePath: 'topics', items: topics },
    },
    {
      label: 'Series',
      to: '/',
      isActive: (path) => path.startsWith('/series'),
      dropdown: { basePath: 'series', items: series },
    },
  ], [topics, series])

  return (
    <header className="bg-secondary-container/80 backdrop-blur-xl border-b border-on-background/5 shadow-sm z-50 sticky top-0">
      <div className="flex justify-between items-center w-full px-margin-mobile md:px-margin-desktop max-w-container-max mx-auto h-16">
        <div className="flex-1 flex justify-start">
          <Link
            to="/"
            className="font-display-lg text-headline-md font-extrabold text-on-background"
          >
            HVChinh
          </Link>
        </div>

        <nav className="hidden md:flex space-x-8 items-center justify-center flex-none h-full">
          {navItems.map(({ label, to, isActive, dropdown }) => {
            const active = isActive(pathname)
            const activeClass =
              'text-on-background font-bold border-b-2 border-on-background pb-1 font-label-md text-label-md'
            const inactiveClass =
              'text-on-surface-variant font-medium font-label-md text-label-md hover:text-on-background transition-colors duration-200'

            return dropdown ? (
              <div key={label} className="relative group h-full flex items-center">
                <Link
                  to={to}
                  className={`flex items-center gap-1 ${active ? activeClass : inactiveClass}`}
                >
                  {label}
                  <ChevronDown
                    size={18}
                    className="transition-transform duration-200 group-hover:rotate-180"
                  />
                </Link>

                <div className="absolute top-full left-0 pt-2 hidden group-hover:block z-50 min-w-[180px]">
                  <div className="bg-surface-white rounded-xl shadow-[0_10px_25px_rgba(0,0,0,0.1)] py-2 border border-outline-variant/20">
                    {dropdown.items.map(({ name, slug }) => (
                      <Link
                        key={slug}
                        to={`/${dropdown.basePath}/${slug}`}
                        className="block px-4 py-2.5 font-label-md text-label-md text-on-surface-variant hover:text-on-background hover:bg-surface-container-low transition-colors"
                      >
                        {name}
                      </Link>
                    ))}
                  </div>
                </div>
              </div>
            ) : (
              <Link
                key={label}
                to={to}
                className={active ? activeClass : inactiveClass}
              >
                {label}
              </Link>
            )
          })}
        </nav>

        <div className="flex items-center space-x-4 flex-1 justify-end" />
      </div>
    </header>
  )
}

export default Header

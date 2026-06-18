import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Search from 'lucide-react/dist/esm/icons/search'

const SearchBox = () => {
  const [query, setQuery] = useState('')
  const navigate = useNavigate()

  const handleSearch = () => {
    const trimmed = query.trim()
    if (trimmed) navigate(`/search?q=${encodeURIComponent(trimmed)}`)
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSearch()
  }

  return (
    <div className="bg-surface-white rounded-xl p-6 shadow-[0_10px_25px_rgba(0,0,0,0.04)]">
      <div className="flex items-center border border-outline-variant/30 rounded-xl overflow-hidden focus-within:border-on-background transition-colors">
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Tìm kiếm bài viết..."
          className="flex-1 pl-4 py-3 bg-transparent focus:outline-none font-body-md text-body-md text-on-background placeholder:text-on-surface-variant/50"
        />
        <button
          onClick={handleSearch}
          aria-label="Tìm kiếm"
          className="px-4 py-3 text-on-surface-variant hover:text-primary hover:bg-surface-dim transition-colors"
        >
          <Search size={20} />
        </button>
      </div>
    </div>
  )
}

export default SearchBox

import ChevronLeft from 'lucide-react/dist/esm/icons/chevron-left'
import ChevronRight from 'lucide-react/dist/esm/icons/chevron-right'

function getPages(current, total) {
  if (total <= 7) {
    return Array.from({ length: total }, (_, i) => i + 1)
  }
  if (current <= 4) {
    return [1, 2, 3, 4, 5, '...', total]
  }
  if (current >= total - 3) {
    return [1, '...', total - 4, total - 3, total - 2, total - 1, total]
  }
  return [1, '...', current - 1, current, current + 1, '...', total]
}

const PAGE_BTN = 'w-10 h-10 flex items-center justify-center rounded-xl font-label-md text-label-md transition-colors'
const IDLE_BTN = `${PAGE_BTN} bg-surface-white border border-outline-variant text-on-surface-variant hover:bg-surface-dim`
const ACTIVE_BTN = `${PAGE_BTN} bg-on-background text-surface-white`

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
  if (totalPages <= 1) return null

  const pages = getPages(currentPage, totalPages)

  return (
    <div className="flex justify-center items-center gap-2 mt-8">
      <button
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 1}
        className={`${IDLE_BTN} disabled:opacity-40 disabled:cursor-not-allowed`}
      >
        <ChevronLeft size={20} />
      </button>

      {pages.map((page, i) =>
        page === '...' ? (
          <span key={`ellipsis-${i}`} className="w-10 h-10 flex items-center justify-center text-on-surface-variant">
            &hellip;
          </span>
        ) : (
          <button
            key={page}
            onClick={() => page !== currentPage && onPageChange(page)}
            className={page === currentPage ? ACTIVE_BTN : IDLE_BTN}
          >
            {page}
          </button>
        )
      )}

      <button
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
        className={`${IDLE_BTN} disabled:opacity-40 disabled:cursor-not-allowed`}
      >
        <ChevronRight size={20} />
      </button>
    </div>
  )
}

export default Pagination

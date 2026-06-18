import { Link } from 'react-router-dom'
import BookOpen from 'lucide-react/dist/esm/icons/book-open'

const SeriesNav = ({ series }) => (
  <div className="bg-surface-white rounded-xl shadow-[0_10px_25px_rgba(0,0,0,0.04)] p-6">
    <div className="flex items-center gap-2 mb-4">
      <BookOpen size={20} className="text-secondary-container shrink-0" />
      <h3 className="font-headline-md text-headline-md text-on-surface">
        Series: {series.name}
      </h3>
    </div>
    <div className="flex flex-col gap-2">
      {series.items.map((item) =>
        item.isCurrent ? (
          <div
            key={item.order}
            className="p-3 rounded-lg bg-primary-container/20 text-on-primary-container font-label-md text-label-md font-bold flex gap-3"
          >
            <span className="text-on-primary-container shrink-0">{item.order}.</span>
            {item.title}
          </div>
        ) : (
          <Link
            key={item.order}
            to={`/posts/${item.slug}`}
            className="p-3 rounded-lg hover:bg-surface-container-high transition-colors font-label-md text-label-md text-on-surface-variant flex gap-3"
          >
            <span className="text-outline shrink-0">{item.order}.</span>
            {item.title}
          </Link>
        )
      )}
    </div>
  </div>
)

export default SeriesNav

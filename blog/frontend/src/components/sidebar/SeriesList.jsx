import { Link } from 'react-router-dom'
import Folder from 'lucide-react/dist/esm/icons/folder'

const SeriesList = ({ series }) => (
  <div className="bg-surface-white rounded-xl p-6 shadow-[0_10px_25px_rgba(0,0,0,0.04)]">
    <h3 className="font-headline-md text-headline-md text-on-background mb-4 pb-2 border-b border-outline-variant/20">
      Series
    </h3>
    <ul className="flex flex-col gap-3">
      {series.map(({ id, name, slug }) => (
        <li key={id}>
          <Link
            to={`/series/${slug}`}
            className="flex items-center gap-2 text-on-background hover:text-primary transition-colors font-label-md text-label-md"
          >
            <Folder size={20} className="text-primary shrink-0" />
            {name}
          </Link>
        </li>
      ))}
    </ul>
  </div>
)

export default SeriesList

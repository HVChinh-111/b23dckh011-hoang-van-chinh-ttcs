import { Link } from 'react-router-dom'

const TopicList = ({ topics }) => (
  <div className="bg-surface-white rounded-xl p-6 shadow-[0_10px_25px_rgba(0,0,0,0.04)]">
    <h3 className="font-headline-md text-headline-md text-on-background mb-4 pb-2 border-b border-outline-variant/20">
      Chủ đề
    </h3>
    <div className="flex flex-wrap gap-2">
      {topics.map(({ name, slug }) => (
        <Link
          key={slug}
          to={`/topics/${slug}`}
          className="px-3 py-1.5 bg-surface-dim hover:bg-primary-container text-on-background font-label-sm text-label-sm rounded-lg transition-colors"
        >
          {name}
        </Link>
      ))}
    </div>
  </div>
)

export default TopicList

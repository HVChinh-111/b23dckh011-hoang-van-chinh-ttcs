import { Link } from 'react-router-dom'
import ArrowRight from 'lucide-react/dist/esm/icons/arrow-right'
import Clock from 'lucide-react/dist/esm/icons/clock'

const PostCard = ({ post, author }) => (
  <article className="bg-surface-white rounded-xl overflow-hidden shadow-[0_10px_25px_rgba(0,0,0,0.04)] border border-transparent transition-all duration-200 card-hover">
    <div className="p-6 flex flex-col gap-3">
      {post.topics?.length > 0 ? (
        <div className="flex flex-wrap gap-2">
          {post.topics.map((topic) => (
            <span
              key={topic.slug}
              className="inline-flex items-center px-3 py-1 rounded-full bg-secondary-container/20 text-on-secondary-container font-label-sm text-label-sm"
            >
              {topic.name}
            </span>
          ))}
        </div>
      ) : null}

      <h2 className="font-headline-md text-headline-md text-on-background">
        {post.title}
      </h2>

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
          <span>{post.date}</span>
        </div>
      ) : null}

      <p className="font-body-md text-body-md text-on-surface-variant line-clamp-2">
        {post.excerpt}
      </p>

      <div>
        <Link
          to={`/posts/${post.slug}`}
          className="font-label-md text-label-md text-primary hover:text-on-background flex items-center transition-colors"
        >
          Xem chi tiết <ArrowRight size={18} className="ml-1" />
        </Link>
      </div>
    </div>
  </article>
)

export default PostCard

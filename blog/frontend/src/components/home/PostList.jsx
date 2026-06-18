import PostCard from './PostCard.jsx'

const PostList = ({ posts, author }) => (
  <div className="flex flex-col gap-8">
    {posts.map((post) => (
      <PostCard key={post.id} post={post} author={author} />
    ))}
  </div>
)

export default PostList

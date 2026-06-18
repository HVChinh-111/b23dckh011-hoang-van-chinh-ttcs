import AuthorBio from './AuthorBio.jsx'
import SearchBox from './SearchBox.jsx'
import TopicList from './TopicList.jsx'
import SeriesList from './SeriesList.jsx'

const Sidebar = ({ author, topics, series }) => (
  <aside className="md:col-span-4 flex flex-col gap-8">
    <AuthorBio author={author} />
    <SearchBox />
    <TopicList topics={topics} />
    <SeriesList series={series} />
  </aside>
)

export default Sidebar

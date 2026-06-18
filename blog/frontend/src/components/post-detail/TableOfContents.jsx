import { useState, useEffect } from 'react'

const TableOfContents = ({ items }) => {
  const [activeId, setActiveId] = useState(items[0]?.id ?? '')

  useEffect(() => {
    const observers = items.map(({ id }) => {
      const el = document.getElementById(id)
      if (!el) return null
      const obs = new IntersectionObserver(
        ([entry]) => {
          if (entry.isIntersecting) setActiveId(id)
        },
        { rootMargin: '-10% 0% -75% 0%' }
      )
      obs.observe(el)
      return obs
    })
    return () => observers.forEach((obs) => obs?.disconnect())
  }, [items])

  return (
    <div className="bg-surface-white rounded-xl shadow-[0_10px_25px_rgba(0,0,0,0.04)] p-6">
      <h3 className="font-headline-md text-headline-md text-on-surface mb-4">Mục lục</h3>
      <nav className="flex flex-col gap-3 border-l-2 border-outline-variant/30 pl-4">
        {items.map(({ id, label, level }) => (
          <a
            key={id}
            href={`#${id}`}
            className={`font-label-md text-label-md transition-colors border-l-2 -ml-[18px] ${
              level === 3 ? 'pl-8 text-[0.8rem]' : 'pl-4'
            } ${
              activeId === id
                ? 'toc-active'
                : 'border-transparent text-on-surface-variant hover:text-on-background'
            }`}
          >
            {label}
          </a>
        ))}
      </nav>
    </div>
  )
}

export default TableOfContents

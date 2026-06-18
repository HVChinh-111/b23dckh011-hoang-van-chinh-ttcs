const AdminTopbar = ({ title }) => (
  <header className="bg-surface-white/80 backdrop-blur-xl border-b border-outline-variant/30 z-10 px-margin-mobile md:px-margin-desktop h-20 flex items-center flex-shrink-0">
    <h2 className="font-headline-md text-headline-md text-on-surface">{title}</h2>
  </header>
)

export default AdminTopbar

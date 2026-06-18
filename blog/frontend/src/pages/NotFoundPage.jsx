import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import Home from 'lucide-react/dist/esm/icons/home'

const NotFoundPage = () => {
  useEffect(() => {
    document.title = '404 - Không tìm thấy | HVChinh Blog'
  }, [])

  return (
  <div className="bg-primary-container text-on-background font-body-md min-h-screen flex flex-col items-center relative overflow-hidden">
    {/* Decorative blur elements */}
    <div className="absolute top-10 left-10 w-32 h-32 bg-surface-white/20 rounded-full blur-3xl pointer-events-none" />
    <div className="absolute bottom-20 right-20 w-64 h-64 bg-secondary-container/30 rounded-full blur-3xl pointer-events-none" />
    <div className="absolute top-1/4 right-1/4 w-16 h-16 bg-surface-white/40 rounded-full blur-xl animate-pulse pointer-events-none" />

    <main className="relative z-10 text-center px-margin-mobile md:px-margin-desktop max-w-container-max mx-auto flex flex-col items-center justify-center gap-8 py-20 flex-1">
      <div className="space-y-4">
        <h1
          className="font-display-lg text-on-background drop-shadow-sm leading-none"
          style={{ fontSize: 'clamp(80px, 15vw, 150px)' }}
        >
          404
        </h1>
        <h2 className="font-headline-lg-mobile md:font-headline-lg text-headline-lg-mobile md:text-headline-lg text-on-primary-container max-w-2xl mx-auto">
          Không tìm thấy nội dung
        </h2>
        <p className="font-body-lg text-body-lg text-on-surface-variant max-w-lg mx-auto pb-4">
          Nội dung bạn đang tìm kiếm không tồn tại hoặc đã bị xóa. Có thể đường dẫn bị sai hoặc
          nội dung đã được di chuyển.
        </p>
      </div>

      <Link
        to="/"
        className="group inline-flex items-center justify-center gap-2 bg-secondary-container hover:bg-secondary-fixed-dim text-on-secondary-container font-label-md text-label-md py-4 px-8 rounded-xl shadow-[0px_10px_25px_rgba(0,0,0,0.04)] hover:shadow-[0px_15px_30px_rgba(0,0,0,0.08)] hover:-translate-y-1 transition-all duration-200 border border-transparent hover:border-on-background/5"
      >
        <Home size={20} className="transition-transform group-hover:-translate-x-1" />
        Quay về trang chủ
      </Link>
    </main>
  </div>
  )
}

export default NotFoundPage

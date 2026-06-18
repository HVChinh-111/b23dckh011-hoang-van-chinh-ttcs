import { Link } from 'react-router-dom'

const FacebookIcon = () => (
  <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
    <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
  </svg>
)

const LinkedInIcon = () => (
  <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
    <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z" />
  </svg>
)

const GitHubIcon = () => (
  <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
    <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12" />
  </svg>
)

const SOCIAL_LINKS = [
  { key: 'facebook', Icon: FacebookIcon, getHref: (a) => a.facebookUrl, bg: 'bg-[#1877F2]', label: 'Facebook' },
  { key: 'linkedin', Icon: LinkedInIcon, getHref: (a) => a.linkedinUrl,  bg: 'bg-[#0A66C2]', label: 'LinkedIn' },
  { key: 'github',   Icon: GitHubIcon,   getHref: (a) => a.githubUrl,    bg: 'bg-[#24292e]', label: 'GitHub'   },
]

const FOOTER_LINKS = [
  { label: 'Về tôi', to: '/about' },
  { label: 'Liên hệ', to: '/contact' },
  { label: 'RSS Feed', to: '/rss' },
]

const LEGAL_LINKS = [
  { label: 'Điều khoản sử dụng', to: '/terms' },
  { label: 'Chính sách bảo mật', to: '/privacy' },
]

const Footer = ({ author }) => (
  <footer
    className="text-on-background py-12 border-t border-outline-variant/30 mt-auto"
    style={{ backgroundColor: 'rgb(255, 245, 202)' }}
  >
    <div className="px-margin-mobile md:px-margin-desktop max-w-container-max mx-auto">
      <div className="grid grid-cols-1 md:grid-cols-12 gap-8 mb-12">
        <div className="md:col-span-6 flex flex-col gap-4">
          <h2 className="font-display-lg text-headline-md font-extrabold">HVChinh</h2>
          <p className="font-body-md text-on-surface-variant max-w-md">
            Blog chia sẻ kiến thức về lập trình, phân tích thiết kế hệ thống và các công nghệ mới.
          </p>
          {author ? (
            <div className="flex gap-3 mt-2">
              {SOCIAL_LINKS.map(({ key, Icon, getHref, bg, label }) => {
                const href = getHref(author)
                return href && href !== '#' ? (
                  <a
                    key={key}
                    href={href}
                    target="_blank"
                    rel="noopener noreferrer"
                    aria-label={label}
                    className={`${bg} text-white w-10 h-10 rounded-full flex items-center justify-center hover:opacity-85 transition-opacity`}
                  >
                    <Icon />
                  </a>
                ) : null
              })}
            </div>
          ) : null}
        </div>

        <div className="md:col-span-3 flex flex-col gap-4">
          <h3 className="font-label-md text-label-md font-bold uppercase tracking-wider">
            Liên kết
          </h3>
          <nav className="flex flex-col gap-2">
            {FOOTER_LINKS.map(({ label, to }) => (
              <Link
                key={to}
                to={to}
                className="font-body-md text-on-surface-variant hover:text-on-background transition-colors"
              >
                {label}
              </Link>
            ))}
          </nav>
        </div>

        <div className="md:col-span-3 flex flex-col gap-4">
          <h3 className="font-label-md text-label-md font-bold uppercase tracking-wider">
            Pháp lý
          </h3>
          <nav className="flex flex-col gap-2">
            {LEGAL_LINKS.map(({ label, to }) => (
              <Link
                key={to}
                to={to}
                className="font-body-md text-on-surface-variant hover:text-on-background transition-colors"
              >
                {label}
              </Link>
            ))}
          </nav>
        </div>
      </div>

      <div className="pt-8 border-t border-on-background/5 flex flex-col md:flex-row justify-between items-center gap-4">
        <p className="font-body-md text-on-surface-variant">© 2026 HVChinh-111. All rights reserved.</p>
        <p className="font-body-md text-on-surface-variant">
          Made with <span className="text-error">❤️</span> by HVChinh-111
        </p>
      </div>
    </div>
  </footer>
)

export default Footer

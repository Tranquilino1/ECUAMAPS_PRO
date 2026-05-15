enum SocialMedia: CaseIterable {
  case telegram
  case twitter
  case instagram
  case facebook
  case reddit
  case matrix
  case fosstodon
  case linkedin
  case ecuamapsEmail
  case github
  case tiktok
  case threads
  case bluesky

  var link: String {
    switch self {
    case .telegram:
      return L("telegram_url")
    case .github:
      return "https://github.com/ecuamaps/ecuamaps/"
    case .linkedin:
      return "https://www.linkedin.com/company/organic-maps/"
    case .ecuamapsEmail:
      return "ios@ecuamaps.app"
    case .matrix:
      return "https://matrix.to/#/#ecuamaps:matrix.org"
    case .fosstodon:
      return "https://fosstodon.org/@ecuamaps"
    case .facebook:
      return "https://facebook.com/ecuamaps"
    case .twitter:
      return "https://twitter.com/ecuamapsApp"
    case .instagram:
      return L("instagram_url")
    case .reddit:
      return "https://www.reddit.com/r/ecuamaps/"
    case .tiktok:
      return "https://www.tiktok.com/@ecuamaps"
    case .threads:
      return "https://www.threads.com/@ecuamaps.app"
    case .bluesky:
      return "https://bsky.app/profile/ecuamaps.bsky.social"
    }
  }

  var image: UIImage {
    switch self {
    case .telegram:
      return UIImage(named: "ic_social_media_telegram")!
    case .github:
      return UIImage(named: "ic_social_media_github")!
    case .linkedin:
      return UIImage(named: "ic_social_media_linkedin")!
    case .ecuamapsEmail:
      return UIImage(named: "ic_social_media_mail")!
    case .matrix:
      return UIImage(named: "ic_social_media_matrix")!
    case .fosstodon:
      return UIImage(named: "ic_social_media_fosstodon")!
    case .facebook:
      return UIImage(named: "ic_social_media_facebook")!
    case .twitter:
      return UIImage(named: "ic_social_media_x")!
    case .instagram:
      return UIImage(named: "ic_social_media_instagram")!
    case .reddit:
      return UIImage(named: "ic_social_media_reddit")!
    case .tiktok:
      return UIImage(named: "ic_social_media_tiktok")!
    case .threads:
      return UIImage(named: "ic_social_media_threads")!
    case .bluesky:
      return UIImage(named: "ic_social_media_bluesky")!
    }
  }
}

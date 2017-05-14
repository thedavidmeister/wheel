(ns wheel.font.config)

; Either matches a key in well-known-fallbacks (below) or is a fallback string.
(def default-fallback "github")

; https://css-tricks.com/snippets/css/system-font-stack/
(def well-known-fallbacks
 {
  "github" "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif, \"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\""
  "medium" "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Oxygen-Sans, Ubuntu, Cantarell, \"Helvetica Neue\", sans-serif"})

(def base-url "https://fonts.googleapis.com/css?family=")

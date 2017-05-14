(ns wheel.font.config)

; Either matches a key in well-known-fallbacks (below) or is a fallback string.
(def default-fallback "github")

; https://css-tricks.com/snippets/css/system-font-stack/
(def well-known-fallbacks
 {
  "github" "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif, \"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\""
  "medium" "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Oxygen-Sans, Ubuntu, Cantarell, \"Helvetica Neue\", sans-serif"})

(def test-examples
 (partition 2
  [{:wheel.font/name ""} ""
   {:wheel.font/name "foo"} "foo"
   {:wheel.font/name "foo bar"} "foo+bar"
   {:wheel.font/name "foo" :wheel.font/variants []} "foo"
   {:wheel.font/name "foo" :wheel.font/variants ["1"]} "foo:1"
   {:wheel.font/name "foo" :wheel.font/variants ["1" "2"]} "foo:1,2"
   {:wheel.font/name "foo bar" :wheel.font/variants ["1" "2"]} "foo+bar:1,2"]))

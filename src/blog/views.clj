(ns blog.views
  (:require [hiccup.page :as page]
            [markdown.core :as md]
            [hickory.core :as hic]
            [hickory.zip :as hiz]
            [clojure.zip :as zip]
            [clojure.string :as cs]))


;; utils
;; ----------------------------------------------------------------------------
(defn md->hiccup
  "Convert markdown file contents into hiccup."
  [filepath]
  (-> filepath
      slurp
      md/md-to-html-string
      hic/parse
      hic/as-hiccup
      hiz/hiccup-zip
      zip/next  ; first item in the list
      zip/next  ; html tag
      zip/next  ; head tag
      ; children of the body tag
      zip/children))

(defn md->article-title
  "Take a filepath to the md article and return its first line that should
  have the following form:
  # ...
  (ie starts with hash followed by space) and returns this line without first
  two characters."
  [filepath]
  (with-open [rdr (clojure.java.io/reader filepath)]
    (-> rdr line-seq first (subs 2))))

(defn list-article-paths
  "Return a list of all .md files of articles in resources/blog_posts/"
  []
  (->> "./docs/resources/blog_posts"
       (clojure.java.io/file)
       file-seq
       (filter #(.isFile %))
       (mapv str)
       (filter #(re-matches #".*md$" %))
       ; need to sort to have the article in the correct chrono order
       sort))

(defn get-article-name
  "Given a filepath (ie './resources/blog_posts/article_name.md') return
  'article_name'.
  NOTE: the filepath is now a java URL object"
  [filepath]
  (->> filepath
      (clojure.java.io/file)
      ;(.getName)  ; for testing only
      (.getPath)
      ((fn [s] (cs/split s #"/")))
      (last)
      ((fn [s] (cs/split s #"\.")))
      (drop-last)
      ((fn [coll] (cs/join "." coll)))))

;; page head, include style sheet
;; ----------------------------------------------------------------------------
(defn gen-page-head
  [title]
  [:head
   [:title title]
   (page/include-css "resources/public/css/styles.css")
   (page/include-css "https://unpkg.com/@highlightjs/cdn-assets@11.9.0/styles/github-dark.css")
   (page/include-js "https://unpkg.com/@highlightjs/cdn-assets@11.9.0/highlight.min.js")
   (page/include-js "https://unpkg.com/@highlightjs/cdn-assets@11.9.0/languages/clojure.min.js")
   (page/include-js "https://unpkg.com/@highlightjs/cdn-assets@11.9.0/languages/python.min.js")
   (page/include-js "https://unpkg.com/@highlightjs/cdn-assets@11.9.0/languages/bash.min.js")])

;; avatar img and monero qr code image
;; ----------------------------------------------------------------------------
(def gen-avatar-img
   [:img.avatar {:src "resources/public/imgs/pixelz1lla.jpg"}])

(def gen-monero-qr-code-image
   [:img.monero-qr {:src "resources/public/imgs/website_address.png"}])

;; side panel common to all of the pages
;; ----------------------------------------------------------------------------
(defn generate-side-panel
  []
  [:div.column.left 
    gen-avatar-img
    [:p "Welcome to dogz1lla's lair"]
    [:p [:a {:href "http://github.com/dogz1lla"} "my github"]]
    [:p (str "If you find information" 
             " on this website" 
             " helpful/interesting/enjoyable"
             " consider donating!")]
    [:p (str "(all donations are" 
             " non-refundable!)")]
    [:p (str " My monero (XMR) wallet:")]
    gen-monero-qr-code-image])

;; header with some useful links
;; ----------------------------------------------------------------------------
(def github-pages-url "https://dogz1lla.github.io/")

(def header-links
  [:div#header-links
   "[ "
   [:a {:href (str github-pages-url "/index.html")} "Title page"]
   " | "
   [:a {:href (str github-pages-url "/home.html")} "List of articles"]
   " ]"])

;; post list
;; ----------------------------------------------------------------------------
(def article-paths (list-article-paths))
(def article-names (mapv get-article-name article-paths))

(defn post-link
  [post-name post-title]
  [:a {:href (str github-pages-url "/" post-name ".html")} post-title])

(defn post-list []
  (map post-link article-names (map md->article-title article-paths)))

(defn list-posts []
  [:ul
   (for [post (post-list)]
     [:li post])])

;; layout common to all pages
;; ----------------------------------------------------------------------------
(defn default-page-layout
  [title contents]
  (page/html5
   (gen-page-head title)
    [:body#title-screen-body
     ; activate code syntax highlighting
     [:script "hljs.highlightAll();"]
     [:div.row
      (generate-side-panel)
      [:div.column.center contents]]]))

;; particular page contents
;; ----------------------------------------------------------------------------
(def title-page-contents 
  [:div#article-page
    header-links
    [:div.centered-block
      [:h1#title-screen-text "Welcome!"]
      [:p "website with articles about stuff."]
      [:a {:href "/home.html"} "click here to enter!"]]])

(def home-page-contents
  [:div#article-page
    header-links
    [:h1 "List of articles:"]
    (list-posts)])

(defn article-page-contents
  [hiccup]
  [:div#article-page
    header-links
    hiccup])

;; views
;; ----------------------------------------------------------------------------
(defn title-page
  []
  (default-page-layout "dogz1lla: title page" title-page-contents))

(defn home-page
  []
  (default-page-layout "dogz1lla: home page" home-page-contents))

(defn article-page
  "Create an html page for the blog post that has been
  written in org mode and exported to the html file
  `filepath`."
  [filepath]
  (default-page-layout 
    (md->article-title filepath)
    (article-page-contents (md->hiccup filepath))))

(comment
  (defn all-methods
    "Print methods of a Java class."
    [x]
    (->> x cr/reflect 
           :members 
           (filter :return-type)  
           (map :name) 
           sort 
           (map #(str "." %) )
           distinct
           println))
  )

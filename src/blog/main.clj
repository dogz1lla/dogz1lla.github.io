;; check out the theme: https://neovim.io/doc/user/lsp.html
(ns blog.main
  (:require [blog.views :as views]))


(defn -main [& args]
  (let [article-htmls (mapv views/article-page views/article-paths)
        article-names (map #(str % ".html") views/article-names)
        article-views (zipmap article-names article-htmls)
        common-views {"index.html" (views/title-page)
                      "home.html" (views/home-page)}
        all-views (merge common-views article-views)
        output-dir "docs"]
    (doseq [[file html] all-views] (spit (str output-dir "/" file) html))))

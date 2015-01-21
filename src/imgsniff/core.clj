(ns imgsniff.core
  (:require [clojure.data.json :as json]
            [clj-http.client :as client]))

(def imgur-client-id "5e439e50bdd53fc")
(def reddit-liked-url "http://www.reddit.com/user/friendlyproject/liked.json")

(defn get-imgur-urls
  [reddit-liked-url]
  (-> (slurp reddit-liked-url)
      (json/read-str)
      (get-in ["data" "children"])
      (->> (map #(get-in % ["data" "url"])))))

(def urls (get-imgur-urls reddit-liked-url))

(pprint urls)

(defn expand-album-url [imgur-url]
  "If imgur-url is an album, return the urls of all its images"
  (if (is-album? imgur-url)
    (get-album-urls imgur-url)
    imgur-url))

; TODO: is this working?
; Then, do get-album-urls
(defn is-album? [imgur-url]
  "Returns true if the url points to an album"
  (let [http-status
        (client/get "https://api.imgur.com/3/album/m9ki8/images"
                    {:headers {"Authorization" (str "Client-ID " imgur-client-id)}})]
    (not (= http-status 404))))

(ns imgsniff.core
  (:require [clojure.data.json :as json]
            [clj-http.client :as client]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def imgur-client-id "5e439e50bdd53fc")

(defn get-urls [reddit-liked-url]
  "Given the url to a user's 'liked' reddit page, return the urls of all
  items."
  (-> (slurp reddit-liked-url)
      (json/read-str)
      (get-in ["data" "children"])
      (->> (map #(get-in % ["data" "url"])))))

(defn is-album? [imgur-url]
  "Returns true if the url points to an album"
  (re-find #"imgur.com/a" imgur-url))

(defn get-image-urls-from-album [imgur-url]
  "Given a url for an imgur album, return the urls of all its images."
  (let [album-id (last (str/split imgur-url #"/"))
        api-url (str "https://api.imgur.com/3/album/" album-id "/images")
        response (client/get api-url {:headers {"Authorization"
                                                (str "Client-ID " imgur-client-id)}})
        parsed-response (json/read-str (:body response))
        image-data (get parsed-response "data")]
    (map #(get % "link") image-data)))

(defn expand-album-url [imgur-url]
  "If imgur-url is an album, return the urls of all its images. Leave non-album
  urls unmolested."
  (if (is-album? imgur-url)
    (get-image-urls-from-album imgur-url)
    [imgur-url]))

(defn expand-album-urls [imgur-urls]
  "Given a seq af imgur urls, replace album urls with urls of all their
  images."
  (mapcat expand-album-url imgur-urls))

(defn save-file-at-url [url]
  "Saves a file given a url. Chooses a file name based on url."
  (with-open [in (io/input-stream url)
              ; TODO: how do I make this go into a different directory?
              out (io/output-stream (last (str/split url #"/")))]
    (io/copy in out)))

(defn save-all-liked-images [reddit-liked-url]
  "Given the url to a reddit user's 'liked' page, download all of the
  imgur images, making sure to grab every image in an album."
  (->> reddit-liked-url
       (get-urls)
       (expand-album-urls)
       (map save-file-at-url)))

#_(save-all-liked-images my-reddit-url)

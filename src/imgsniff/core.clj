(ns imgsniff.core
  (:require [clojure.data.json :as json]
            [clj-http.client :as client]
            [clojure.string :as str]))

(def imgur-client-id "5e439e50bdd53fc")
(def reddit-liked-url "http://www.reddit.com/user/friendlyproject/liked.json")

(defn get-imgur-urls
  [reddit-liked-url]
  (-> (slurp reddit-liked-url)
      (json/read-str)
      (get-in ["data" "children"])
      (->> (map #(get-in % ["data" "url"])))))

(def urls (get-imgur-urls reddit-liked-url))


(defn is-album? [imgur-url]
  "Returns true if the url points to an album"
  (re-find #"imgur.com/a" imgur-url))

(assert (is-album? "http://imgur.com/a/m9ki8"))
(assert (not (is-album? "http://i.imgur.com/ggMoxgg.jpg"))


(defn expand-album-url [imgur-url]
  "If imgur-url is an album, return the urls of all its images"
  (if (is-album? imgur-url)
    (get-image-urls-from-album imgur-url)
    [imgur-url]))

#_(expand-album-url "http://imgur.com/a/m9ki8")
#_(expand-album-url "http://imgur.com/not-an-album-yo")
#_(expand-album-url "http://i.imgur.com/ggMoxgg.jpg")


(defn get-image-urls-from-album [imgur-url]
  "Given a url for an imgur album, return the urls of all its images."
  (let [album-id (last (str/split imgur-url #"/"))
        api-url (str "https://api.imgur.com/3/album/" album-id "/images")
        response (client/get api-url
                             {:headers {"Authorization" (str "Client-ID " imgur-client-id)}})
        parsed-response (json/read-str (:body response))
        data (get parsed-response "data")]
    (map #(get % "link") data)))


(defn expanded-urls [imgur-urls]
  "Given a seq af imgur urls, replace album urls with urls of all their images.
  Leave non-album urls unmolested."
  (mapcat expand-album-url imgur-urls))

(expanded-urls ["http://imgur.com/a/m9ki8"
                "http://i.imgur.com/ggMoxgg.jpg"])




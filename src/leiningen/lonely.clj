(ns leiningen.lonely
    (:require [lonely.core :as lonely]
              [clojure.string :as string])
    (:import (java.io File)))

(defn lonely
  [project & namespaces]
  (if (seq namespaces)
    (doseq [lonely-ns (->> (map symbol namespaces)
                           (lonely/lonely-nses (File. (:root project) "src"))
                           (seq))]
      (println lonely-ns))
    (leiningen.core.main/abort "Specify entrypoint namespaces")))

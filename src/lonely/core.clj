(ns lonely.core
  (:require [me.raynes.fs :as fs]
            [clojure.set :as set]
            [clojure.java.io :as io]
            [loom.graph :as graph]
            [loom.alg :as alg]))

(defn ns-symbol
  [ns-decl]
  {:post [(symbol? %)]}
  (second ns-decl))

(defn ns-requires
  [ns-decl]
  {:pre [(= 'ns (first ns-decl))
         (symbol? (second ns-decl))]
   :post [(every? symbol? %)]}
  (->> (drop 2 ns-decl)
       (filter #(and (list? %) (= (first %) :require)))
       (nfirst)
       (map #(if (sequential? %) (first %) %))))

(defn read-nses
  [dir]
  (->> (fs/iterate-dir dir)
       (mapcat (fn [[root dirs files]]
                 (map #(str root "/" %) files)))
       (filter #(re-find #"\.clj$" %))
       (map (comp read-string slurp))))

(defn ns-deps
  [dir]
  (->> (read-nses dir)
       (map (juxt ns-symbol ns-requires))
       (into {})))

(defn ns-graph
  [dir]
  (let [successors (ns-deps dir)
        nodes (set (keys successors))
        successors (into {} (map #(vector (first %) (set/intersection nodes (set (last %)))) successors)) ]
    (graph/fly-graph :nodes nodes :successors successors)))

(defn lonely-nses
  [dir main-nses]
  (let [graph (ns-graph dir)]
    (apply set/difference (graph/nodes graph) (map #(set (alg/bf-traverse graph %)) main-nses))))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

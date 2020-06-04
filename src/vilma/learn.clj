(ns vilma.learn
  (:require
   [clojure.spec.alpha :as s]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]
   [vilma.utils :as utils]))


(defn fit [X y]
  (let [avgX (utils/average X)
        avgy (utils/average y)]
    10))

(st/instrument)

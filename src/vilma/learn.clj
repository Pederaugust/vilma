(ns vilma.learn
  (:require
   [clojure.spec.alpha :as s]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]
   [vilma.utils :as utils]
   [vilma.statistics :as stat]))

(defn least-squares-predict [{:keys [a b]} x]
  (+ a (* b x)))

(defn sum-squared-errors [model X y]
  (utils/round (utils/reduce-indexed (fn [acc elem i]
                                       (+ (utils/squared (- elem
                                                            (least-squares-predict model (nth X i))))
                                          acc)) y)))

(defn sum-squares-total [y avgy]
  (stat/variance-numerator y avgy))

(defn r2-score [model X y avgy]
  (utils/round (- 1 (/ (sum-squared-errors model X y)
                       (sum-squares-total y avgy)))))

(defn least-squares [X y]
  (let [avgX (utils/average X)
        avgy (utils/average y)
        M (stat/variance-numerator X avgX)
        B (/ (stat/covariance-numerator X y avgX avgy)
             M)
        model {:a (- avgy (* B avgX))
               :b B}]
    (assoc model :r2-score (r2-score model X y avgy))))

(st/instrument)

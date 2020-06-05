(ns vilma.learn
  (:require
   [clojure.spec.alpha :as s]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]
   [vilma.utils :as utils]
   [vilma.statistics :as stat]))


(defmulti predict :model-type)

(defmethod predict :least-squares
  [{:keys [a b] :as model} x]
  (if (sequential? x)
    (map (fn [e] (predict model e)) x)
    (+ a (* b x))))

(defn sum-squared-errors [model X y]
  (utils/round (utils/reduce-indexed (fn [acc elem i]
                                       (+ (utils/square (- elem
                                                           (predict model (nth X i))))
                                          acc)) y)))

(defn sum-squares-total [y avgy]
  (stat/variance-numerator y avgy))

(defn r2-score [SSE SST]
  (utils/round (- 1 (/ SSE
                       SST))))



(defn least-squares
  "Calculate the regression line with least squares algorithm.
  Input: `X` Training Data, `y` target values
  Returns: a model that can be used to predict y for x"
  [X y]
  (let [avgX (utils/average X)
        avgy (utils/average y)
        M (stat/variance-numerator X avgX)
        B (/ (stat/covariance-numerator X y avgX avgy)
             M)
        A (- avgy (* B avgX))
        SSE (sum-squared-errors {:model-type :least-squares :a A :b B} X y)
        SST (sum-squares-total y avgy)
        r2 (r2-score SSE SST)]

    {:model-type :least-squares
     :a A
     :b B
     :r2-score r2
     :SSE SSE
     :SST SST}))


(st/instrument)

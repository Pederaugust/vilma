(ns vilma.statistics
  (:require
   [clojure.spec.alpha :as s]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]
   [vilma.utils :as utils]))


(defn-spec variance-numerator float?
  [X sequential?, avgX number?]
  (float (reduce (fn [acc element]
                   (+ (utils/exponent (- element avgX) 2)
                      acc))
                 0 X)))

(defn-spec population-variance float?
  [X sequential?]
  (utils/round (/ (variance-numerator X (utils/average X))
                  (count X))))

(defn-spec sample-variance float?
  [X sequential?]
  (utils/round (/ (variance-numerator X (utils/average X))
                  (- (count X) 1))))

(defn-spec standard-deviation float?
  [variance number?]
  (utils/round (utils/square-root variance)))

(defn-spec sample-standard-deviation float?
  [X sequential?]
  (standard-deviation (sample-variance X)))

(defn-spec population-standard-deviation float?
  [X sequential?]
  (standard-deviation (population-variance X)))

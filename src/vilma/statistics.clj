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


(defn-spec covariance-numerator float? [X sequential?, Y sequential?, avgX number?, avgY number?]
  (->> (map (fn [x y] (* (- x avgX)
                         (- y avgY)))
            X Y)
       (reduce +)))

(defn sample-covariance

  ([X Y]
   (let [avgX (utils/average X)
         avgY (utils/average Y)]
     (sample-covariance X Y avgX avgY)))

  ([X Y avgX avgY]
   (if (= (count X) (count Y))
     (utils/round (/ (covariance-numerator X Y avgX avgY)
                     (- (count X) 1)))
     (throw (AssertionError. "X and Y needs to be the same length")))))

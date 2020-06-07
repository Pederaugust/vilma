(ns vilma.utils
  (:require
   [clojure.spec.alpha :as s]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]))

(defn-spec exponent number?
  "Takes `x` and `n`, Returns power of `x` to the `n`"
  [x number?, n int?]
  (Math/pow x n))

(defn-spec eulers number?
  [n number?]
  (Math/exp n))

(defn-spec square number?
  [x number?]
  (* x x))

(defn-spec round float?
  "Takes `x`, Rounds `x` to the nearest 2 decimal places"
  [x number?]
  (/ (Math/round (* x 100.0)) 100.0))

(defn-spec average float?
  [numbers sequential?]
  (float (/ (reduce + numbers) (count numbers))))

(defn-spec square-root float?
  [x number?]
  (float (Math/sqrt x)))



(st/instrument)

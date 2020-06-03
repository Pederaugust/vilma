(ns vilma.utils
  (:require
   [clojure.spec.alpha :as s]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]))

(s/def ::indexed-reduce-fn (s/fspec :args (s/cat :acc number?
                                                 :element number?
                                                 :index int?)))
(s/def ::indexable-collection (s/or :list list?
                                    :vector vector?))


(defn-spec exponent number?
  "Takes `x` and `n`, Returns power of `x` to the `n`"
  [x number?, n int?]
  (Math/pow x n))

(defn-spec round float?
  "Takes `x`, Rounds `x` to the nearest 2 decimal places"
  [x number?]
  (/ (Math/round (* x 100.0)) 100.0))

"
(s/fdef reduce-indexed
  :args (s/alt :two-arity (s/cat :f ::indexed-reduce-fn
                                 :coll ::indexable-collection)
               :three-arity (s/cat :f ::indexed-reduce-fn
                                   :init some?
                                   :coll ::indexable-collection))
  :ret any?)
"
(defn reduce-indexed
  "Reduces a collection `coll` with a function `f`. Alternatively an initial value `init`.
  The function has to have an additional argument which specifies the index.
  Example f: (fn [acc element index] (if (odd? index) (+ acc element) acc)"

  ([f coll]
   (reduce-indexed f 0 coll))

  ([f init coll]
   (:sum (reduce (fn [{sum :sum index :index} element]
                  {:sum (f sum element index)
                   :index (inc index)})

                 {:sum init
                  :index 0}
                 coll))))


(st/instrument)

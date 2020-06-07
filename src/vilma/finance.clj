(ns vilma.finance
  (:require
   [clojure.spec.alpha :as s]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]
   [vilma.utils :as utils]))

(s/def ::cashflow (s/or :list list?
                        :vector vector?))
(s/def ::discount-rate number?)
(s/def ::interest-rate number?)
(s/def ::growth-rate number?)
(s/def ::n int?)
(s/def ::future-value number?)
(s/def ::present-value number?)
(s/def ::payment number?)
(s/def ::present-value-args (s/keys* :req-un [::discount-rate]
                                     :opt-un [::cashflow ::n ::future-value ::payment ::growth-rate]))
(s/def ::future-value-args (s/keys* :req-un [::n ::interest-rate ::present-value]))


(defn discount-factor
  [discount-rate n]
  (/ 1 (utils/exponent (+ 1 (/ discount-rate 100)) n)))

(defn-spec simple-present-value float?
  "Takes a `future-value` and discounts it with `n` years in the future. Uses a `discount-rate` in percent.
   Returns the discounted present value"
  [future-value number?, n int?, discount-rate ::discount-rate]

  (utils/round (* future-value (discount-factor discount-rate n))))


(defn-spec discount-cashflow float?
  "Takes collection of `cash-flow` starting from year 1. Discounts each year with the `discount-rate`. Returns present value"
  [cashflow ::cashflow, discount-rate ::discount-rate]

  (if (empty? cashflow)
    0.0
    (->> (map (fn [element index]
                (simple-present-value element (+ index 1) discount-rate))
              cashflow
              (range (count cashflow)))
         (reduce +)
         utils/round)))



(s/fdef perpetuity
  :args (s/alt :two-arity (s/cat :payment ::payment :discount-rate ::discount-rate)
               :three-arity (s/cat :payment ::payment :discount-rate ::discount-rate :growth-rate ::growth-rate))
  :ret float?)
(defn perpetuity
  "Calculates the value of an infinite perpetuity.
   Takes yearly `payment` and `discount-rate` and optional `growth-rate`"

  ([payment discount-rate]
   (perpetuity payment discount-rate 0))

  ([payment discount-rate growth-rate]
   (utils/round (/ payment (- (/ discount-rate 100) (/ growth-rate 100))))))

(s/fdef annuity
  :args (s/alt :three-arity (s/cat :payment ::payment :discount-rate ::discount-rate :n ::n)
               :four-arity (s/cat :payment ::payment :discount-rate ::discount-rate :growth-rate ::growth-rate :n ::n))
  :ret float?)
(defn annuity
  "Calculates the present value of an annuity with or without growth"
  ([payment discount-rate n]
   (annuity payment discount-rate 0 n))

  ([payment discount-rate growth-rate n]
   (let [growth (/ growth-rate 100)
         discount (/ discount-rate 100)]

     (utils/round (* payment (/ (- 1 (* (discount-factor discount-rate n)
                                        (utils/exponent (+ 1 growth) n)))
                                (- discount growth)))))))


(defn-spec present-value ::present-value
  "Calculates the present value.
  If the arguments are sufficient to find it.
  Possible argument combinations:
    Simple cashflow: {`cashflow`, `discount-rate`}
    Discounting a future-value, `n` years in the future: {`future-value`, `n`, `discount-rate`}
    Non-growing Perpetuity (equal payments every year, forever): {`payment`, `discount-rate`}
    Growing Perpetuity (growing payments every year, forever): {`payment`, `discount-rate` `growth-rate`}
    Annuity: {`payment` `discount-rate` `n`}
    Growing annuity: {`payment` `discount-rate` `growth-rate` `n`}

  Example:
  ```clojure
  (present-value :cashflow [100 100 -10 100] :discount-rate 10)

  ;; or
  (present-value :future-value 100 :n 10 :discount-rate 5)

  ;; or value of non-growing perpetuity
  (present-value :payment 10 :discount-rate 10)
  ;; or value of growing perpetuity
  (present-value :payment 10 :discount-rate 10 :growth-rate 10)

  ;; or for annuity
  (present-value :payment 10 :discount-rate 10 :n 10)
  ;; or for annuity with growth
  (present-value :payment 10 :discount-rate 10 :n 10 :growth-rate 5)
  ```"

  [& {:keys [future-value n discount-rate cashflow payment growth-rate]} ::present-value-args]

  (cond (= discount-rate 0)
        0.0

        (and payment discount-rate growth-rate n)
        (annuity payment discount-rate growth-rate n)

        (and payment discount-rate n)
        (annuity payment discount-rate n)

        (and payment discount-rate growth-rate)
        (perpetuity payment discount-rate growth-rate)

        (and payment discount-rate)
        (perpetuity payment discount-rate)

        (and cashflow discount-rate)
        (discount-cashflow cashflow discount-rate)

        (and future-value n discount-rate)
        (simple-present-value future-value n discount-rate)

        :else
        (throw (AssertionError. "Wrong argument combination, check the argument list"))))


(defn-spec future-value ::future-value
  "Calculates simple compound interest of a `present-value`.
  Possible argument combinations:
     Compounding present-value, `n` years in the future: {`present-value`, `n`, `interest-rate`}

  Example:
  ```clojure
  (future-value :present-value 100 :n 10 :interest-rate 5)
  ```"
  [& {:keys [present-value interest-rate n]} ::future-value-args]

  (utils/round (* present-value
                  (utils/exponent (+ 1 (/ interest-rate 100))
                                  n))))


(st/instrument)

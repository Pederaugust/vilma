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
(s/def ::n int?)
(s/def ::future-value number?)
(s/def ::present-value number?)
(s/def ::present-value-args (s/alt :cashflow-bias (s/keys* :req-un [::cashflow ::discount-rate])
                                   :single-value-bias (s/keys* :req-un [::n ::discount-rate ::future-value])))

(s/def ::future-value-args (s/keys* :req-un [::n ::interest-rate ::present-value]))


(defn-spec simple-present-value float?
  "Takes a `future-value` and discounts it with `n` years in the future. Uses a `discount-rate` in percent. Returns the discounted present value"
  [future-value number?, n int?, discount-rate ::discount-rate]
  (utils/round (/ future-value
                  (utils/exponent (+ 1 (/ discount-rate 100))
                                  n))))


(defn-spec discount-cashflow float?
  "takes collection of `cash-flow` starting from year 1. Discounts each year with the `discount-rate`. Returns present value"
  [cashflow ::cashflow, discount-rate ::discount-rate]
  (if (empty? cashflow) 0.0
      (utils/reduce-indexed (fn [acc element index]
                              (+ (simple-present-value element (+ index 1) discount-rate)
                                 acc))
                            cashflow)))


(defn-spec present-value ::present-value
  "Depending on the inputmap it calculates the present value if the arguments are sufficient to find it"
  [& {:keys [future-value n discount-rate cashflow]} ::present-value-args]
  (cond (and cashflow discount-rate)
        (discount-cashflow cashflow discount-rate)

        (and future-value n discount-rate)
        (simple-present-value future-value n discount-rate)))


(defn-spec future-value ::future-value
  [& {:keys [present-value interest-rate n]} ::future-value-args]
  (cond (and present-value interest-rate n)
        (utils/round (* present-value
                        (utils/exponent (+ 1 (/ interest-rate 100))
                                        n)))))


(st/instrument)

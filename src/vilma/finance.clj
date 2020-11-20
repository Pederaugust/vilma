(ns vilma.finance
  (:require
   [clojure.spec.alpha :as s]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]
   [vilma.utils :as utils :refer [absolute]]))

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

(s/def ::future-value-args (s/keys* :opt-un [::n ::interest-rate ::present-value ::payment ::growth-rate]))

(s/def ::discount-rate-args (s/keys* :opt-un [::cashflow ::n ::future-value ::present-value ::payment ::growth-rate]))

(s/def ::payment-args (s/keys* :opt-un [::n ::future-value ::growth-rate ::interest-rate]))

(defn discount-factor
  [discount-rate n]
  (/ 1 (utils/exponent (+ 1 (/ discount-rate 100)) n)))

(defn-spec simple-present-value float?
  "Takes a `future-value` and discounts it with `n` years in the future. Uses a `discount-rate` in percent.
   Returns the discounted present value"
  [future-value number?, n int?, discount-rate ::discount-rate]

  (utils/round (* future-value (discount-factor discount-rate n))))


(defn-spec discount-cashflow float?
  "Takes collection of `cash-flow` starting from year 0. Discounts each year with the `discount-rate`. Returns present value"
  [cashflow ::cashflow, discount-rate ::discount-rate]

  (if (empty? cashflow)
    0.0
    (->> (map (fn [element index]
                (simple-present-value element index discount-rate))
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


(defn stock-price
  [& {:keys [year-one-dividend discount-rate growth-rate year-one-price year-one-eps pvgo]}]
  (utils/round (cond (and year-one-dividend discount-rate year-one-price)
                     (/ (+ year-one-dividend year-one-price)
                        (+ 1 (/ discount-rate 100)))

                     (and year-one-dividend discount-rate growth-rate)
                     (/ year-one-dividend
                        (/ (- discount-rate growth-rate) 100))

                     (and year-one-eps discount-rate pvgo)
                     (+ (/ year-one-eps (/ discount-rate 100))
                        pvgo)

                     :else
                     (throw (AssertionError. "Wrong argument combination stock-price")))))


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

  ;; or for year-one-dividend with price-growth (stocks)
  (present-value :year-one-dividend 10 :year-one-price 110 :discount-rate 5)
  ;; or for year-one-dividend with growth in dividend
  (present-value :year-one-dividend 10 :discount-rate 8 :growth-rate 5)
  ;; Present value of stock with year one eps (earnings per share) and with net present value of investment in growth PVGO
  (present-value :year-one-eps 10 :discount-rate 8 :pvgo 10)
  ```"

  [& {:keys [future-value n discount-rate cashflow payment growth-rate
             year-one-dividend year-one-price year-one-eps pvgo]} ::present-value-args]

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

        (and year-one-dividend discount-rate year-one-price)
        (stock-price :year-one-dividend year-one-dividend
                     :discount-rate discount-rate
                     :year-one-price year-one-price)

        (and year-one-dividend discount-rate growth-rate)
        (stock-price :year-one-dividend year-one-dividend
                     :discount-rate discount-rate
                     :growth-rate growth-rate)

        (and year-one-eps pvgo discount-rate)
        (stock-price :year-one-eps year-one-eps
                     :discount-rate discount-rate
                     :pvgo pvgo)

        :else

        (throw (AssertionError. "Wrong argument combination, check the argument list"))))


(defn payment-of-annuity-fv
  ([interest-rate n future-value]
   (payment-of-annuity-fv interest-rate n future-value 0))
  ([interest-rate n future-value growth-rate]
   (let [growth (/ growth-rate 100)
         interest (/ interest-rate 100)]
     (/ future-value
        (/ (- (utils/exponent (+ 1 interest)
                              n)
              (utils/exponent (+ 1 growth)
                              n))
           (- interest growth))))))


(defn payment-of-annuity-pv
  ([discount-rate n present-value]
   (payment-of-annuity-fv discount-rate n present-value 0))
  ([discount-rate n present-value growth-rate]
   (let [growth (/ growth-rate 100)
         discount (/ discount-rate 100)]
     (/ present-value
        (/ (- 1 (utils/exponent (/ (+ 1 growth)
                                   (+ 1 discount))
                                n))
           (- discount growth))))))


(defn-spec payment ::payment
  "Calculate payment of annuity from future value
  Possible argument combinations:
     `n` years in the future: {`future-value`, `n`, `interest-rate`}

  Example:
  ```clojure
  (future-value :present-value 100 :n 10 :interest-rate 5)
  ```"
  [& {:keys [interest-rate n growth-rate future-value present-value]} ::payment-args]
  (utils/round (cond (and interest-rate n future-value growth-rate)
                     (payment-of-annuity-fv interest-rate n future-value growth-rate)

                     (and interest-rate n future-value)
                     (payment-of-annuity-fv interest-rate n future-value)

                     (and interest-rate n present-value growth-rate)
                     (payment-of-annuity-pv interest-rate n present-value growth-rate)

                     (and interest-rate n present-value)
                     (payment-of-annuity-pv interest-rate n present-value)

                     :else
                     (throw (AssertionError. "Wrong argument combination, check the argument list")))))

(defn-spec simple-discount-rate ::discount-rate
  [present-value number?, future-value number?,  n number?]
  (utils/round (* (- (utils/exponent (/ future-value
                                        present-value)
                                     (/ 1
                                        n))
                     1)
                  100)))

(def tolerance 0.01)

(defn inverse-quadratic-interpolation [a b c fa fb fc]
  (+ (/ (* a fb fc)
        (* (- fa fb) (- fa fc)))
     (/ (* b fa fc)
        (* (- fb fa) (- fb fc)))
     (/ (* c fa fb)
        (* (- fc fa) (- fc fb)))))

(defn secant-method [a b fa fb]
  (- b (* fb (/ (- b a)
                (- fb fa)))))

(defn bisection-method? [a b c fa fb fc s mflag d]
  (or (not (<= (/ (+ (* 3 a) b)
                  4)
               s b))
      (and mflag (>= (absolute (- s b)) (/ (absolute (- b c)) 2)))
      (and (not mflag) (>= (absolute (- s b)) (/ (absolute (- c d)) 2)))
      (and mflag (< (absolute (- b c)) tolerance))
      (and (not mflag) (< (absolute (- c d)) tolerance))))

(defn bisection [a b]
  (/ (+ a b)
     2))

(defn calculate-s [a b c fa fb fc mflag d]
  (let [s (if (and (not= fa fc) (not= fb fc))
            (inverse-quadratic-interpolation a b c fa fb fc)
            (secant-method a b fa fb))]
    (if (bisection-method? a b c fa fb fc s mflag d)
      {:s (bisection a b)
       :mflag true}
      {:s s
       :mflag false})))

(defn initialize [a b f]
  (let [fa (f a)
        fb (f b)]
    (if (< (absolute fa) (absolute fb))
      {:fa fb :fb fa :a b :b a}
      {:fa fa :fb fb :a a :b b})))


(defn brents-method [a b f]
  (let [{fa :fa fb :fb a :a b :b} (initialize a b f)]

    (if (< (* fa fb) 0)
      (loop [a a b b c a
             fa fa fb fb fc fa
             mflag true
             d a]

        (let [{s :s mflag :mflag} (calculate-s a b c fa fb fc mflag d)
              fs (f s)]

          (cond (or (= fb 0) (= fs 0) (< (absolute (- b a)) tolerance))
                b
                (and (< (* fa fs) 0) (< (absolute fa) (absolute fb)))
                (recur s a b (f s) fa fb mflag c)
                (and (>= (* fa fs) 0) (< (absolute fa) (absolute fb)))
                (recur b s b fb (f s) fb mflag c)

                (and (< (* fa fs) 0) (>= (absolute fa) (absolute fb)))
                (recur a s b fa (f s) fb mflag c)
                (and (>= (* fa fs) 0) (>= (absolute fa) (absolute fb)))
                (recur s b b (f s) fb fb mflag c))))
      (throw (RuntimeException. "Hey there")))))


(defn-spec internal-rate-of-return ::discount-rate
  [cashflow sequential?]
  (brents-method 1000000000000000 0.9 (partial discount-cashflow cashflow)))



(defn-spec discount-rate ::discount-rate
  "Calculates the discount-rate."
  [& {:keys [future-value n present-value cashflow payment growth-rate]} ::discount-rate-args]

  (cond (and present-value n future-value)
        (simple-discount-rate present-value future-value n)

        cashflow
        (internal-rate-of-return cashflow)

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
  [& {:keys [present-value interest-rate n payment growth-rate]} ::future-value-args]
  (-> (cond (and present-value interest-rate n payment growth-rate)
            10.0
            (and payment interest-rate n growth-rate)
            (let [interest (/ interest-rate 100)
                  growth (/ growth-rate 100)]
              (* payment
                 (/ (- (utils/exponent (/ (+ 1 interest)
                                          (+ 1 growth))
                                       n)
                       1)
                    (- interest growth))))

            (and payment interest-rate n)
            (let [interest (/ interest-rate 100)]
              (* payment
                 (/ (- (utils/exponent (+ 1 interest)
                                       n)
                       1)
                    interest)))

            (and present-value interest-rate n)
            (* present-value
               (utils/exponent (+ 1 (/ interest-rate 100))
                               n))

            :else
            (throw (AssertionError. "Wrong argument combination")))
      utils/round))


(st/instrument)

(ns vilma.finance-test
  (:require [vilma.finance :as finance]
            [midje.sweet :refer :all]))

(facts "About present value"
       (fact "present value calculates correct PV for a one time in-amount"
             (finance/present-value :future-value 100000 :n 10 :discount-rate 5) => 61391.33
             (finance/present-value :future-value 10 :n 7 :discount-rate 10) => 5.13)
       (fact "present value of a negative number is negative"
             (finance/present-value :future-value -100000 :n 10 :discount-rate 5) => -61391.33)

       (fact "present value of a series of incomming cash flow calculates correctly"
             (finance/present-value :cashflow [10 10 10] :discount-rate 5) => 28.59
             (finance/present-value :cashflow [10 -10 10] :discount-rate 5) => 9.55
             (finance/present-value :cashflow [-10 -10 -10] :discount-rate 5) => -28.59)

       (fact "present value of a cash flow of only one calculates correctly"
             (finance/present-value :cashflow [10] :discount-rate 5) => 10.0)
       (fact "present value of a cash flow of none calculates correctly"
             (finance/present-value :cashflow [] :discount-rate 5) => 0.0)

       (fact "present value of a series 0 incomming cash flow gives a present value of 0"
             (finance/present-value :cashflow [0 0 0] :discount-rate 5) => 0.0)

       (fact "present value of a series with discount-rate of zero returns 0"
             (finance/present-value :cashflow [0 0 0] :discount-rate 0) => 0.0)

       (fact "Present value with a bad argument-map"
             (finance/present-value :crash-clow [0 0 0] :baaaad 5) => (throws RuntimeException))
       (fact "Bad arguments throws exception"
             (finance/present-value :bad 0) => (throws RuntimeException))
       (fact "No arguments throws exception"
             (finance/present-value ) => (throws RuntimeException)))


(facts "Present value of a perpetuity"
       (fact "present-value calculates perpetuity properly"
             (finance/present-value :payment 10 :discount-rate 5) => 200.00)
       (fact "present-value with perpetuity and discount-rate = 0 returns 0"
             (finance/present-value :payment 10 :discount-rate 0) => 0.0)
       (fact "growing perpetuity calculates properly"
             (finance/present-value :payment 10 :discount-rate 5 :growth-rate 3) => 500.0)
       (fact "growing perpetuity calculates properly with growth-rate of 0"
             (finance/present-value :payment 10 :discount-rate 5 :growth-rate 0) => 200.00)
       (fact "growing perpetuity calculates properly with payment of 0"
             (finance/present-value :payment 0 :discount-rate 5 :growth-rate 0) => 0.00)
       (fact "growing perpetuity calculates properly with growth-rate bigger than oportunity cost of capital (discount-rate) "
             (finance/present-value :payment 10 :discount-rate 5 :growth-rate 7) => -500.00))

(facts "Present value of an annuity"
       (fact "present-value calculates annuity properly"
             (finance/present-value :payment 100 :discount-rate 5 :n 10) => 772.17)
       (fact "present-value discount-rate = 0 returns 0"
             (finance/present-value :payment 10 :discount-rate 0 :n 10) => 0.0)
       (fact "present-value of annuity with growth works"
             (finance/present-value :payment 10 :discount-rate 10 :n 10 :growth-rate 5) => 74.40)
       (fact "present-value of annuity with growth works when growth-rate is 0"
             (finance/present-value :payment 10 :discount-rate 10 :n 10 :growth-rate 0) => 61.45)
       (fact "present-value of annuity with growth works when all variables are negative"
             (finance/present-value :payment -10 :discount-rate -10 :n -10 :growth-rate 0) => 65.13))

(facts "Payment")
       ;(fact "Payment calculates correctly for annuity with future-value"
             ;(finance/payment :interest-rate 5 :n 10 :future-value 1000.00) => 30.7
       ;(fact "Payment calculates correctly for annuity with future-value with growth ")
             ;(finance/payment :interest-rate 10 :n 10 :future-value 1000.00 :growth-rate 5) => 51.82)
       ;(fact "Payment calculates correctly for annuity with present-value "
             ;(finance/payment :interest-rate 5 :n 10 :present-value 1000.00) => 129.5)
       ;(fact "Payment calculates correctly for annuity with present-value with growth "
             ;(finance/payment :interest-rate 10 :n 10 :present-value 1000.00 :growth-rate 5) => 134.41))


(facts "About future value"
       (fact "Normal compounding works"
             (finance/future-value :present-value 100 :n 10 :interest-rate 5) => 162.89)

       (fact "Compounding 0 returns 0"
             (finance/future-value :present-value 0 :n 10 :interest-rate 5) => 0.00)

       (fact "Bad arguments throws exception"
             (finance/future-value :bad 0) => (throws AssertionError))
       (fact "No arguments throws exception"
             (finance/future-value :bad 0) => (throws AssertionError)))
       ;; (fact "Future value of payments"
       ;;       (finance/future-value :payment 10
       ;;                             :n 10
       ;;                             :interest-rate 10) => 159.37)

       ;; (fact "Future value of payments with growth"
       ;;       (finance/future-value :payment 10
       ;;                             :n 10
       ;;                             :interest-rate 10
       ;;                             :growth-rate 5) => 74.4)

       ;; (fact "Future value of payments with present-value"
       ;;       (finance/future-value :payment 10
       ;;                             :n 10
       ;;                             :interest-rate 10
       ;;                             :present-value 100) => -418.75)

       ;; (fact "Future value of payments with growth and present-value"
       ;;       (finance/future-value :payment 10
       ;;                             :present-value 100
       ;;                             :n 10
       ;;                             :interest-rate 10
       ;;                             :growth-rate 5) => 100.0))


(facts "future-value in relation to present-value"
       (fact "future-value with same arguments should discount to present-value with same arguments"
             (finance/present-value :future-value (finance/future-value :present-value 100.0
                                                                        :n 10
                                                                        :interest-rate 5)
                                    :n 10
                                    :discount-rate 5) => 100.0)

       (fact "present-value with same arguments should discount to future-value with same arguments"
             (finance/future-value :present-value (finance/present-value :future-value 100.0
                                                                         :n 10
                                                                         :discount-rate 5)
                                   :n 10
                                   :interest-rate 5) => 100.0))

(facts "Present value of stocks"
       (fact "present-value calculates year one dividend and price correctly"
             (finance/present-value :year-one-dividend 10
                                    :year-one-price 110
                                    :discount-rate 5) => 114.29)

       (fact "present-value of dividend with growth"
             (finance/present-value :year-one-dividend 10
                                    :discount-rate 8
                                    :growth-rate 5) => 333.33)

       (fact "present-value with EPS and PVGO (net present value of investment in growth)"
             (finance/present-value :year-one-eps 10
                                    :discount-rate 8
                                    :pvgo 10) => 135.0))






;; (facts "About discount-rate"
;;        (fact "calculates correct simple discount-rate"
;;              (finance/discount-rate :future-value 100000 :n 10 :present-value 61391.33 ) => 5.0
;;              (finance/discount-rate :future-value 10 :n 7 :present-value 5.13 ) => 10.0)
;;        (fact "Calculates correct internal rate of return of a cashflow"
;;              (finance/discount-rate :cashflow [-10 10 11]) => 66.19)
;;        (fact "Calculates correct internal rate of return of a cashflow large numbers"
;;              (finance/discount-rate :cashflow [-1000 1000 1100]) => 66.19)
;;        (fact "Calculates correct internal rate of return of a cashflow long lasting cashflows"
;;              (finance/discount-rate :cashflow [-1000 1000 1100 1100 1100 1100 1100 1100 1100 1100]) => 104.71)
;;        (fact "with a bad argument-map"
;;              (finance/discount-rate :crash-clow [0 0 0] :baaaad 5) => (throws AssertionError))
;;        (fact "Bad arguments throws exception"
;;              (finance/discount-rate :bad 0) => (throws AssertionError))
;;        (fact "No arguments throws exception"
;;              (finance/discount-rate) => (throws AssertionError)))

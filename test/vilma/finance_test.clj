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
             (finance/present-value :cashflow [10 10 10] :discount-rate 5) => 27.23
             (finance/present-value :cashflow [10 -10 10] :discount-rate 5) => 9.09
             (finance/present-value :cashflow [-10 -10 -10] :discount-rate 5) => -27.23)

       (fact "present value of a cash flow of only one calculates correctly"
             (finance/present-value :cashflow [10] :discount-rate 5) => 9.52)
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
             (finance/present-value :bad 0) => (throws RuntimeException)))


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


(facts "About future value"
       (fact "Normal compounding works"
             (finance/future-value :present-value 100 :n 10 :interest-rate 5) => 162.89)

       (fact "Compounding 0 returns 0"
             (finance/future-value :present-value 0 :n 10 :interest-rate 5) => 0.00)

       (fact "Bad arguments throws exception"
             (finance/future-value :bad 0) => (throws RuntimeException))
       (fact "No arguments throws exception"
             (finance/future-value :bad 0) => (throws RuntimeException)))


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

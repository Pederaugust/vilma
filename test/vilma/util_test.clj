(ns vilma.util-test
  (:require  [midje.sweet :refer :all]
             [vilma.utils :as utils]))

(facts "About exponent"
       (fact "Exponent of 0 works for n = 1 and n = 0"
             (utils/exponent 0 0) => 1.0
             (utils/exponent 0 1) => 0.0)
       (fact "Exponent of 2 works"
             (utils/exponent 2 2) => 4.0
             (utils/exponent 2 4) => 16.0
             (utils/exponent 2 0) => 1.0))

(facts "about round"
       (fact "rounding of 0.00"
             (utils/round 0.00) => 0.00
             (utils/round 0.0001) => 0.00)
       (fact "rounding with alot of decimals is correct"
             (utils/round 10.123456789) => 10.12)
       (fact "high rounding ceils it"
             (utils/round 10.178) => 10.18)
       (fact "low rounding floors it"
             (utils/round 10.112) => 10.11))


(facts "about average"
       (fact "average of numbers"
             (utils/average [1 2 3]) => 2.0
             (utils/average [1 2 3 4]) => 2.5))

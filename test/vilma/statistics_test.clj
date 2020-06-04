(ns vilma.statistics-test
  (:require  [midje.sweet :refer :all]
             [vilma.statistics :as stat]))

(facts "About variance"
       (fact "variance-numerator calculates correctly"
             (stat/variance-numerator [1 2 3 4 5 6] 3.5) => 17.5)
       (fact "population-variance calculates correctly"
             (stat/population-variance [1 2 3 4 5 6]) => 2.92)
       (fact "sample-variance calculates correctly"
             (stat/sample-variance [1 2 3 4 5 6]) => 3.5))

(facts "About standard-deviation"
       (fact "standard deviation works for variance input"
             (stat/standard-deviation 3.5) => 1.87)
       (fact "Sample standard deviation works for X"
             (stat/sample-standard-deviation [1 2 3 4 5 6]) => 1.87)
       (fact "population standard deviation works for X"
             (stat/population-standard-deviation [1 2 3 4 5 6]) => 1.71))

(facts "About covariance"
       (fact "Calculates correct covariance"
             (stat/sample-covariance [1 2 3 4] [4 3 2 1]) => -1.67)
       (fact "Throws assertionerror if the lists are the wrong length in relation to each other"
             (stat/sample-covariance [1 2 3 4] [4 3 2 1 10]) => (throws AssertionError))
       (fact "Calculates correct covariance numerator"
             (stat/covariance-numerator [1 2 3 4] [4 3 2 1] 2.5 2.5) => -5.0))

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

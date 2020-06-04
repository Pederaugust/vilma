(ns vilma.learn-test
  (:require  [midje.sweet :refer :all]
             [vilma.learn :as learn]))

(facts "Regression"
       (let [X [1 2 3 4]
             y [2 6 5 8]
             model (learn/least-squares X y)]

         (fact "Calculates correctly a"
                 (:a model) => 1.0)
         (fact "Calculates correctly b"
               (:b model) => 1.7)
         (fact "sum of squared errors works"
               (learn/sum-squared-errors model X y) => 4.3)
         (fact "sum of squares total works"
               (learn/sum-squares-total y 5.25) => 18.75)
         (fact "r2 score is correct"
               (learn/r2-score model X y 5.25) => 0.77))
       (fact "r2-score of total correlation is 1"
             (learn/r2-score (learn/least-squares [1 2 3 4] [1 2 3 4])
                             [1 2 3 4]
                             [1 2 3 4]
                             2.5) => 1.0))

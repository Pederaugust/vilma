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
               (learn/r2-score 4.3 18.75) => 0.77))

       (let [X [1 2 3 4]
             y [1 2 3 4]
             model (learn/least-squares X y)]

         (fact "predict works for one number"
               (learn/predict model 5) => 5.0)
         (fact "predict works for sequences"
               (learn/predict model [1 8]) => [1.0 8.0])
         (fact "r2-score of 100 % when numbers align"
               (:r2-score model) => 1.0)))


;(facts "Multivariable linear regression"
       ;(let [X [[1 2] [2 2] [3 1] [4 2]]
             ;y [1 2 3 4]
             ;model (learn/least-squares X y))
         ;(fact "Calculates correctly a"
                 ;(:a model) => 1.0)))

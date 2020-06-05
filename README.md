# Vilma

Statistical and financial clojure. 


## Usage

### Learn package
This package contains more advanced statistical functions for prediction
``` clojure
(ns my-namespace.core
    (:require [vilma.learn :as learn]))
```

#### Creating models
There is currently only one model. Single variable Least squares linear regression. 

``` clojure
(def my-model (learn/least-squares [1 2 3 4 5] [2 4 6 8 10]))

;; => {:a 0.0, :b 2.0, :r2-score 1.0}
```
#### Predicting outcomes
when you have your model, you can start to predict:

``` clojure
(learn/predict my-model 10)
;; => 20.0
```

### Statistics package
This package contains standard statistic functions
``` clojure
(ns my-namespace.core
    (:require [vilma.statistics :as stat]))
```

#### Calculating variance and standard deviation
``` clojure
(stat/population-variance [1 2 3 4 5 6])
;; => 2.92

(stat/sample-variance [1 2 3 4 5 6]) 
;; => 3.5

;; For standard deviation you can either use the general function that takes variance as an argument
(stat/standard-deviation 3.5) 
;; => 1.87
;; This can be more efficient especially if you already have the variance

;; Or use the standard deviation functions if you're not interested in the variance
(stat/sample-standard-deviation [1 2 3 4 5 6]) 
;; => 1.87

(stat/population-standard-deviation [1 2 3 4 5 6]) 
;; => 1.71

;; Sample covariance
(stat/sample-covariance [1 2 3 4] [4 3 2 1])
;; => -1.67

```

### Finance package
This package is used for standard financial calculation. Designed to take named arguments.
Throws a RuntimeException if the information is not sufficient.

```clojure
(ns my-namespace.core
  (:require [vilma.finance :as fin])

```

Arguments to functions are non-positional, so the order doesn't matter.
#### present-value function
```clojure

;; Simple present value of a future value
(fin/present-value :future-value 100 
                   :n 10 
                   :discount-rate 5)
;; => 61.39

;; Present value of cashflow
(fin/present-value :cashflow [100 100 -10 100] 
                   :discount-rate 10)
;; => 234.34

;; Present value of non-growing perpetuity
(fin/present-value :payment 10 
                   :discount-rate 10)
;; => 1000.00

;; Present value of growing perpetuity
(fin/present-value :payment 10 
                   :discount-rate 10 
                   :growth-rate 5)
;; => 500.00

;; Present value of non-growing annuity
(fin/present-value :payment 10 
                   :discount-rate 10 
                   :n 10) 
;; => 61.45

;; Present value of growing annuity
(fin/present-value :payment 10 
                   :discount-rate 10 
                   :n 10 
                   :growth-rate 5) 
;; => 74.40

``` 

#### future-value function
```clojure

(fin/future-value :present-value 100 
                  :n 10 
                  :interest-rate 5)
;; => 162.89

```

You can't input just anything (even though it seems like it).
There are very specific combination of inputs that work. (And more to come)

#### Possible inputmaps for present-value
```clojure
{:future-value :n :discount-rate}
;; or
{:cashflow :discount-rate}
;; or for perpetuity
{:payment :discount-rate}
;; or perpetuity with growth
{:payment :discount-rate :growth-rate}
;; or for annuity
{:payment :discount-rate :n}
;; or for annuity with growth
{:payment :discount-rate :growth-rate :n}
```

#### Possible inputmaps for future-value
```clojure
{:present-value :n :interest-rate}
```

### Future plans
In the future I plan to implement learning algorithms like linear regression and other algorithms you find in scikit learn.
I also plan on implementing a neural network.
For the financial functions I plan on implementing functions that enable you to find the discount-rate, growth-rate,
n, payment.

## License

Copyright © 2020 

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

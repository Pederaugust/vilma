# Vilma

Statistical and financial clojure. 


## Usage

### Finance package
This package is used for standard financial calculation. Designed to take named arguments.
Throws a RuntimeException if the information is not sufficient.

```clojure
(ns vilma.finance-test
  (:require [vilma.finance :as fin])

```

#### present-value function
```clojure

;; Simple present value of a future value
(fin/present-value :future-value 100 :n 10 :discount-rate 5)
;; => 61.39

;; Present value of a cash flow
(fin/present-value :cashflow [100 100 -10 100] :discount-rate 10)
;; => 234.34

;; Present value of non-growing perpetuity
(fin/present-value :payment 10 :discount-rate 10)
;; => 1000.00

;; Present value of growing perpetuity
(fin/present-value :payment 10 :discount-rate 10 :growth-rate 5)
;; => 500.00

;; Present value of non-growing annuity
(fin/present-value :payment 10 :discount-rate 10 :n 10) 
;; => 61.45

;; Present value of growing annuity
(fin/present-value :payment 10 :discount-rate 10 :n 10 :growth-rate 5) 
;; => 74.40

``` 

#### future-value function
```clojure

(fin/future-value :present-value 100 :n 10 :interest-rate 5)
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

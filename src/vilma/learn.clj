(ns vilma.learn
  (:require
   [clojure.core.matrix :as mat]
   [clojure.core.matrix.operators :as op]
   [clojure.core.matrix.random :refer [randoms]]
   [clojure.spec.alpha :as s]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]
   [vilma.utils :as utils :refer [eulers average square]]
   [vilma.statistics :as stat]))

(mat/set-current-implementation :vectorz)


;; The fundamental models that have the ability to use the function: predict
(s/def ::model-type #{:least-squares :neuralnet})

;; Specs for neuralnet
(s/def ::nodes int?)
(s/def ::activation #{:sigmoid :relu})
(s/def ::proto-layer (s/keys* :req-un [::nodes ::activation]))
(s/def ::layer (s/keys :req-un [::nodes ::activation]))

;; Specs for linear regression
(s/def ::a number?)
(s/def ::b number?)
(s/def ::SSE number?)
(s/def ::SST number?)
(s/def ::r2-score number?)

(s/def ::least-squares-model (s/keys :req-un [::a ::b ::model-type]
                                     :opt-un [::SSE ::SST ::r2-score]))



(defmulti predict :model-type)
(defmethod predict :default
  [model x]
  (prn "oops you need to set a model-type"))

(defmethod predict :least-squares
  [{:keys [a b] :as model} x]
  (if (sequential? x)
    (map (fn [e] (predict model e)) x)
    (+ a (* b x))))


(defn-spec sum-squared-errors ::SSE
  [model ::least-squares-model, X sequential?, y sequential?]
  (->> (map (fn [a b] (square (- a (predict model b))))
            y X)
       (reduce +)
       utils/round))


(defn-spec sum-squares-total ::SST
  [y sequential?, avgy number?]
  (stat/variance-numerator y avgy))


(defn-spec r2-score ::r2-score
  [SSE ::SSE, SST ::SST]
  (utils/round (- 1 (/ SSE
                       SST))))


(defn-spec least-squares ::least-squares-model
  "Calculate the regression line with least squares algorithm.
  Input: `X` Training Data, `y` target values
  Returns: a model that can be used to predict y for x"
  [X sequential?, y sequential?]

  (let [avgX (average X)
        avgy (average y)
       
        M (stat/variance-numerator X avgX)
        B (/ (stat/covariance-numerator X y avgX avgy)
             M)

        A (- avgy (* B avgX))
        SSE (sum-squared-errors {:model-type :least-squares :a A :b B} ;; Temporary model
                                X y)

        SST (sum-squares-total y avgy)
        r2 (r2-score SSE SST)]

    {:model-type :least-squares
     :a A
     :b B
     :r2-score r2
     :SSE SSE
     :SST SST}))




(defn- generate-weight-matrix [m n]
  (mat/matrix (map (fn [elem]
                     (take m (repeatedly #(rand))))
                   (range n))))

(defn sigmoid [M]
  (mat/logistic M))

(defn relu [M]
  (mat/signum M))

(defn softmax [M]
  (let [sum (mat/ereduce (fn [acc element] (+ acc (eulers element)))
                         M)]
    (mat/emap #(/ (eulers %)
                  sum)
              M)))



(defn-spec create-layer map?
  [{:keys [nodes activation] :as layer} ::layer]
  (case activation
    :relu
    (assoc layer :activation relu)

    :sigmoid
    (assoc layer :activation sigmoid)

    :softmax
    (assoc layer :activation softmax)))

(defn-spec dense-layer map?
  "Simple helper function to make initialization of the neural net a
little more friendly to the user"
  [& {:keys [activation nodes] :as layer} ::proto-layer]
  (-> {:activation activation
       :nodes nodes}
      (create-layer ,,,)
      (assoc ,,, :layer-type :dense)))


(defn- initialize-layer
  [{:keys [nodes] :as layer} previous-layer-shape]
  (assoc layer :weights (generate-weight-matrix nodes previous-layer-shape)))


(defn- find-layer-output [{:keys [layers] :as net} x]
  (let [first-hidden (first layers)
        remaining (rest layers)
        first-activation (:activation first-hidden)
        first-weights (:weights first-hidden)]

    (reduce (fn [outputs {activation :activation weights :weights}]
              (conj outputs (activation (mat/mmul (last outputs) weights))))
            [(first-activation (mat/mmul (mat/array x)
                                         first-weights))]
            remaining)))


(defmethod predict :neuralnet
  [{:keys [layers] :as model} x]
  (last (find-layer-output model x)))

(defn neuralnet-init [& {:keys [input-shape layers]}]
  {:model-type :neuralnet
   :input-shape input-shape
   :layers (:current-list (reduce (fn [acc layer]
                                    {:previous-layer-shape (:nodes layer)
                                     :current-list (conj (:current-list acc)
                                                         (initialize-layer layer (:previous-layer-shape acc)))})
                                  {:previous-layer-shape input-shape
                                   :current-list []}
                                  layers))})

(defn adjust-weights [{:keys [layers learning-rate] :as neuralnet} x y]
  (let [outputlist (find-layer-output neuralnet x)] ;; Do the prediction and create the layer outputs

    (loop [output outputlist
           index (- (count outputlist) 1)
           error (mat/square (op/- (last outputlist) y))
           newNet neuralnet] ;; Find the first errors ;; ;;
      (if (empty? output)
        newNet
        (do
          (prn index)
          (recur (butlast output)
                 (- index 1)
                 (mat/square (op/- (get-in neuralnet [:layers index :weights])
                                   error))


                 (assoc-in newNet [:layers (- index 1) :weights]
                           (op/* learning-rate (mat/mmul (op/* error output (op/- 1.0 output))
                                                         (nth outputlist index))))))))))



(defn fit [{:keys [layers learning-rate] :as neuralnet} X y]
  "hei")





(st/instrument)

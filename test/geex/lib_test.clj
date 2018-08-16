(ns geex.lib-test
  (:require [geex.java :as java :refer [typed-defn]]
            [geex.core :as core]
            [geex.lib :as lib]
            [clojure.test :refer :all]))

(typed-defn add-3 [Double/TYPE a
                   Double/TYPE b
                   Double/TYPE c]
            (lib/+ a b c))

(deftest add-3-test
  (is (= (add-3 9.0 4.1 1.15)
         14.25)))

(typed-defn and-3
            [Boolean/TYPE a
             Boolean/TYPE b
             Boolean/TYPE c
             ]
            (lib/and a b c))

(defn gen-combos [n]
  (if (= n 1)
    [[true] [false]]
    (transduce
     (comp (map (fn [combo]
                  [(conj combo false) (conj combo true)]))
           cat)
     conj
     []
     (gen-combos (dec n)))))

(defn true-and-3 [a b c]
  (and a b c))

(deftest and-3-test
  (doseq [combo (gen-combos 3)]
    (let [actual (apply and-3 combo)
          expected (apply true-and-3 combo)]
      (is (= actual expected))
      (when (not= actual expected)
        (println "Combo" combo)
        (println "Actual" actual)
        (println "Expected" expected)))))


(typed-defn or-3
            [Boolean/TYPE a
             Boolean/TYPE b
             Boolean/TYPE c]
            (lib/or a b c))

(defn true-or-3 [a b c]
  (or a b c))

(deftest test-or-3
  (doseq [combo (gen-combos 3)]
    (is (= (apply or-3 combo)
           (apply true-or-3 combo)))))

(typed-defn add-with-constant
            [Long/TYPE x]
            (lib/+ x 119))

(deftest add-with-constant-test
  (is (= 121 (add-with-constant 2))))

(typed-defn my-negate [Double/TYPE x]
            (lib/- x))

(deftest my-negate-test
  (is (= -3.0 (my-negate 3))))

(typed-defn my-sub [Double/TYPE a
                    Double/TYPE b]
            (lib/- a b))

(deftest my-sub-test
  (is (= -314.0
         (my-sub 10 324))))

(typed-defn my-sub-3 [Double/TYPE a
                      Double/TYPE b
                      Double/TYPE c]
            (lib/- a b c))

(deftest sub-3-test
  (is (= -12.0
         (my-sub-3 1 4 9))))

(typed-defn my-not [Boolean/TYPE x]
            (lib/not x))

(deftest not-test
  (is (= false (my-not true)))
  (is (= true (my-not false))))

(typed-defn my-implies [Boolean/TYPE a
                        Boolean/TYPE b]
            (lib/implies a b))

(deftest implies-test
  (is (true? (my-implies false false)))
  (is (true? (my-implies false true)))
  (is (false? (my-implies true false)))
  (is (true? (my-implies true true))))

(typed-defn my-raw-eq-test [Long/TYPE a
                            Long/TYPE b]
            (lib/== a b))

(deftest raw-eq-test
  (is (my-raw-eq-test 9 9))
  (is (not (my-raw-eq-test 9 8))))

(typed-defn in-interval? [Long/TYPE a]
            (lib/and (lib/<= 4 a)
                     (lib/<= a 9)))

(deftest in-interval-test
  (is (in-interval? 4))
  (is (not (in-interval? 0))))

(typed-defn compare-against-119 [Long/TYPE x]
            [(lib/== 119 x)
             (lib/<= 119 x)
             (lib/>= 119 x)
             (lib/< 119 x)
             (lib/> 119 x)
             (lib/!= 119 x)
             (lib/= 119 x)
             (lib/not= 119 x)
             ])

(deftest compare-agains-119-test
  (is (= [true true true false false false true false]
         (compare-against-119 119)))
  (is (= [false false true false true true false true]
         (compare-against-119 118)))
  (is (= [false true false true false true false true]
         (compare-against-119 120))))


;; TODO: Comparison of general objects.
(typed-defn eq-ops [clojure.lang.IPersistentVector a
                    clojure.lang.IPersistentVector b]
            (lib/== a b))

(deftest test-common-eq
  (let [x [:a :b :c]]
    (is (eq-ops x x))
    (is (not (eq-ops x [:a :b])))))

(typed-defn mixed-add [Double/TYPE a
                       Long/TYPE b]
            (lib/+ a b))



(deftest mixed-add-test
  (is (= 7.0 (mixed-add 3 4))))


(typed-defn fn-returning-nil [Long/TYPE x]
            (core/If (lib/< x 9)
                     (lib/wrap "Less than 9")
                     (lib/nil-of java.lang.String)))

(deftest check-nil-ret
  (is (= "Less than 9" (fn-returning-nil 1)))
  (is (nil? (fn-returning-nil 19))))

(typed-defn div-120 [Double/TYPE x]
            (lib// 120.0 x))

(deftest div-test
  (is (= 40.0 (div-120 3))))
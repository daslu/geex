(ns geex.java.defs
  (:require [clojure.spec.alpha :as spec]))

(spec/def ::typed-argument (spec/cat :type any?
                                     :name symbol?))

(spec/def ::typed-arguments (spec/spec (spec/* ::typed-argument)))

(spec/def ::meta (spec/or :debug #{:debug}))

(spec/def ::defn-args (spec/cat :name symbol?
                                :meta (spec/* ::meta)
                                :arglist ::typed-arguments
                                :body (spec/* any?)))

;; https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html

(def binary-math-operators ["+" "-" "*" "/" "<" "<=" ">=" ">"])

(defn to-bool [x]
  (if x true false))

(defn and-fn [a b]
  (to-bool (and a b)))

(defn or-fn [a b]
  (to-bool (or a b)))

(defn not-fn [x]
  (to-bool (not x)))

(defn generalize-binary-fn [f]
  (fn [& args] (reduce (completing f) args)))



(def operator-info-map (merge
                        (into {} (map (fn [s]
                                        [s {:clojure-fn (eval (symbol s))
                                            :name s}])
                                      binary-math-operators))
                        {"==" {:clojure-fn =
                               :name "=="}
                         "!=" {:clojure-fn not=
                               :name "!="}
                         "%" {:clojure-fn mod
                              :name "%"}


                         ;;; TODO: The compiled Java code involoving these operators
                         ;;; will be lazily evaluated, even though the Clojure code
                         ;;; may not reflect that. For instance, we might bind something
                         ;;; to a local variable, and in the end the expression bound to that
                         ;;; local variable gets directly inserted in the generated Java code.
                         ;;; So even if we might think it will get evaluated, it might not be
                         ;;; evaluated.
                         ;;
                         ;; Because probably these and/or ops will just compile down to
                         ;; ifs and gotos in the JVM bytecode, we could as well express them
                         ;; with macros that generate if forms.
                         "&&" {:clojure-fn (generalize-binary-fn and-fn)
                               :name "&&"}
                         "||" {:clojure-fn (generalize-binary-fn or-fn)
                               :name "||"}

                         "!" {:clojure-fn not-fn
                              :name "!"}

                         "~" {:clojure-fn bit-not
                              :name "~"}
                         "<<" {:clojure-fn bit-shift-left
                               :name "<<"}
                         ">>" {:clojure-fn bit-shift-right
                               :name ">>"}
                         ">>>" {:clojure-fn unsigned-bit-shift-right
                                :name ">>>"}
                         "&" {:clojure-fn bit-and
                              :name "&"}
                         "^" {:clojure-fn bit-flip
                              :name "^"}
                         "|" {:clojure-fn bit-or
                              :name "|"}
                         }))
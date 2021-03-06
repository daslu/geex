(ns geex.core.stringutils)

;; Convention: Whenever risk of ambiguity,
;; a function should wrap its output in parenthesis.
;; But it is not its responsibility to wrap its input.
(defn wrap-in-parens [x]
  ["(" x ")"])

(defn join-spaced
  ([x] x)
  ([a b]
   (str a " " b)))

(defn nested-to-string
  [x]
  (cond
    (string? x) x
    (number? x) (str x)
    (sequential? x) (transduce
                     (map nested-to-string)
                     join-spaced
                     ""
                     x)
    :default (throw (ex-info "Cannot convert to string"
                             {:value x}))))

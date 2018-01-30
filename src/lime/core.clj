(ns lime.core
  (:require [bluebell.utils.party :as party]
            [clojure.spec.alpha :as spec]))

;; State used during meta-evaluation
(def state (atom {::last-dirty nil
                  ::requirements []
                  ::dirty-counter 0}))

(spec/def ::seed (spec/keys :req [::type
                                  ::compiler
                                  ::deps]))

;; Test if something is a seed
(defn seed? [x]
  (spec/valid? ::seed x))

;; Access the last dirty
(def last-dirty (party/key-accessor ::last-dirty))

;; Access the requirements
(def requirements (party/key-accessor ::requirements))

;; Access the dirty-counter
(def dirty-counter (party/key-accessor ::dirty-counter))

;; Increase the counter of the state map
(def inc-counter #(party/update % dirty-counter inc))

;; Helper for with-requirements
(defn append-requirements [r s]
  (party/update s requirements #(into % r)))

(defn with-requirements [r f]
  (assert (fn? f))
  (let [initial-reqs (-> state deref requirements)
        new-reqs (swap! state (partial append-requirements r))
        result (f)
        old-reqs (swap! state #(requirements % initial-reqs))]
    result))

;; Associate the requirements with random keywords in a map,
;; so that we can merge it in deps.
(defn make-req-map []
  (into {} (map (fn [x] [(keyword (gensym "req")) x])
                (-> state deref requirements))))


;; Special access to a dirty, if any
(def dirty (party/key-accessor ::dirty))


;; The dependencies of a seed
(def deps (party/key-accessor ::deps))

;; The compiler of a seed
(def compiler (party/key-accessor ::compiler))

;; Access the datatype of the seed
(def datatype (party/key-accessor ::type))

;; Create a new seed, with actual requirements
(defn initialize-seed [] (-> {}
                             (deps (make-req-map))
                             (compiler nil)
                             (datatype nil)))

;; Extend the deps map
(defn add-deps [dst extra-deps]
  (party/update dst deps #(merge % extra-deps)))

;; Access the last dirty in the deps map
(def last-dirty-dep (party/chain deps last-dirty))

;; Call this function when a seed has been constructed,
;; but is side-effectful
(defn dirty [x]
  (last-dirty
   (swap! state
          (fn [s]
            (inc-counter
             (last-dirty
              s
              (-> x
                  (dirty-counter (dirty-counter s))
                  (last-dirty-dep (last-dirty s)))))))))

(def prev-dirty (party/key-accessor ::prev-dirty))

(defn replace-dirty [s new-dirty]
  (-> s
      (prev-dirty (last-dirty s))
      (last-dirty new-dirty)))

(defn record-dirties [initial-dirty f]
  (let [prev (-> state deref :last-dirty)]))

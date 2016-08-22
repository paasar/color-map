(ns color-map.core
  (:require [cljs.core :refer [clj->js]]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def all-black (into {} (for [x (range  256) y (range 256)] [[x y] [0 0 0]])))

(defn trace [n]
  (js/console.log n)
  n)

(def step (atom :static))

(defonce discord (atom 0))

(defn update-discord-div []
  (let [div (. js/document getElementById "discord")]
    (set! (. div -innerHTML) @discord)))

(defn ^:export doshuffle [] (reset! step :shuffle))
(defn ^:export greet [] (js/alert "Hi!"))

(defn vec-to-map [pixels]
  (into {} (map (fn [{:keys [x y color]}] [[x y] color]) pixels)))

(defn get-discord [[r1 g1 b1] [r2 g2 b2]]
  (if (or (nil? r1) (nil? r2))
    0
    (+ (Math.abs (- r1 r2)) (Math.abs (- g1 g2)) (Math.abs (- b1 b2)))))

(defn get-total-discord [pixels]
  (apply + (for [x (range 256)
                 y (range 256)]
             (+ (get-discord (get pixels [x y])
                              (get pixels [(inc x) y]))
                (get-discord (get pixels [x y])
                              (get pixels [x (inc y)]))))))

(defn update-full-discord [pixels]
  (->> pixels
       get-total-discord
       (reset! discord)))

(defn shuffle-pixels [pixels]
  (let [colors (vals pixels)
        shuffled (shuffle colors)]
    (:pixels (reduce (fn [{:keys [x y pixels]} color]
                       (let [ny (if (= x 255) (inc y) y)
                             nx (if (= x 255) 0 (inc x))]
                         {:x nx
                          :y ny
                          :pixels (merge pixels {[x y] color})}))
                     {:x 0 :y 0 :pixels {}}
                     shuffled))))

(defn create-pixels []
  (into {} (for [x (range 256)
                 y (range 256)]
             [[x y]
              [(- 255 x) y (- x y)]
;              [255 0 0]
             ])))

  (defn setup []
    ; Set frame rate to 30 frames per second.
    ;  (q/frame-rate 30)
    (q/frame-rate 1)
    (q/color-mode :rgb)
    (let [state {:pixels (create-pixels)}
          state_ {:pixels (merge all-black {[100 100] [255 0 0]
                                            [101 100] [255 0 0]
                                            [101 101] [255 0 0]
                                            [100 101] [255 0 0]})}]
      (update-full-discord (:pixels state))
      (update-discord-div)
      state))

(defn update-state [state]
  (condp = @step
    :static state
    :shuffle (do (reset! step :recalculate)
                 (update state :pixels shuffle-pixels))
    :recalculate (do
                   (update-full-discord (:pixels state))
                   (update-discord-div)
                   state)
    :else state))

(defn rotate-color [state]
  {:pixels (into {} (mapv (fn [{:keys [color] [x y] :pos}]
                            (let [[r g b] color
                                  incr #(if (= % 255) 0 (inc %))
                                  nr (incr r)
                                  ng (incr g)
                                  nb (incr b)]
                              {:pos [x y]
                               :color [nr ng nb]}))
                          (:pixels state)))})

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 0)
  (doseq [[[x y] color] (:pixels state)]
    (q/set-pixel x y (apply q/color color))))

(q/defsketch color-map
  :host "color-map"
  :size [256 256]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])

(ns color-map.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(defn setup []
  ; Set frame rate to 30 frames per second.
;  (q/frame-rate 1)
  (q/color-mode :rgb)
  {:pixels (for [x (range 255)
                 y (range 255)]
             {:x x
              :y y
              :color [x (Math/round (/ (+ x y)  2)) y]
              ;:color [200 200 200]
              })})

(defn update-state [state]
  state)

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 0)
  (doseq [pix (:pixels state)]
    (q/set-pixel (:x pix)
                 (:y pix)
                 (apply q/color (:color pix)))))

(q/defsketch color-map
  :host "color-map"
  :size [255 255]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])

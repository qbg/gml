(ns gml.examples.temperature
  (:require [gml.gml :as gml]))

(defn- parse-double
  [^String s]
  (try
    (Double/parseDouble s)
    (catch NumberFormatException e 0)))

(defn- f-to-c
  [gui]
  (let [f (parse-double (gml/get-property gui :f :text))
	c (* 5/9 (- f 32))]
    (swap! (gml/gui-model gui) assoc :f f :c c)))

(defn- c-to-f
  [gui]
  (let [c (parse-double (gml/get-property gui :c :text))
	f (+ 32 (* 9/5 c))]
    (swap! (gml/gui-model gui) assoc :f f :c c)))

(defn start
  []
  (gml/make-gui
   (atom {:f 32 :c 0})
   [:window {:title "Temp converter" :size [300 100]}
    [:grid {:layout {:rows 2 :columns 2}}
     [:label {:text "Fahrenheit"}]
     [:label {:text "Celsius"}]
     [:text {:text #(str (double (:f %))) :name :f :action f-to-c}]
     [:text {:text #(str (double (:c %))) :name :c :action c-to-f}]]]))

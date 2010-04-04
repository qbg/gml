(ns gml.examples.buttons
  (:use [gml gui]))

(defn lots-of-buttons
  "Display lots of buttons"
  []
  (create-gui
   (window {:title "Lots of buttons"}
    (vertical
     (for [y (range 10)]
       (horizontal
	(for [x (range 10)]
	  (button (str y x)))))))))


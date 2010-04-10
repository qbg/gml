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

(defn options-window
  "Display some buttons in a window"
  []
  (create-gui
   (window {:title "Example" :name :window}
     (vertical
      (horizontal
       (button {:name :1} "One")
       (button {:name :2} "Two")
       (button {:name :3} "Three"))
      (button {:name :quit} "Quit")))
   ([:1 :action] #(.setTitle (by-name :window) "One"))
   ([:2 :action] #(.setTitle (by-name :window) "Two"))
   ([:3 :action] #(.setTitle (by-name :window) "Three"))
   ([:quit :action] #(.dispose (by-name :window)))))

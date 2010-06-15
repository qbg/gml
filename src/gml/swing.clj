(ns gml.swing
  (:require [gml.translate :as translate])
  (:import [java.awt EventQueue Dimension]
	   [java.awt.event ActionListener]))

(declare components)
(declare getters)
(declare setters)

(defn run-in-gui
  [f]
  (EventQueue/invokeLater f))

(defn make-component
  [state type children props]
  ((components type) state children props))

(defn- add-children
  [component children]
  (doseq [c children]
    (.add component c)))

(defn set-prop
  [component prop val]
  (if-let [setter (setters prop)]
    (if (sequential? val)
      (apply setter component val)
      (setter component val))
    (throw (IllegalArgumentException.
	    (format "Property %s not recognized" prop)))))

(defn get-prop
  [component prop]
  (if-let [getter (getters prop)] 
    (getter component)
    (throw (IllegalArgumentException.
	    (format "Property %s not recognized" prop)))))

(defn- set-props
  [component state props]
  (doseq [[prop val] (seq props)]
    (if (fn? val)
      (swap! (:update-list state) conj #(set-prop component prop (val %)))
      (set-prop component prop val))))

(defn- add-listener
  [component state f]
  (let [gui (:gui state)
	al
	(reify
	 ActionListener
	 (actionPerformed [_ e]
	   (let [source (.getSource e)]
	     (f @gui))))]
    (if f
      (.addActionListener component al))))


(defn- build-standard
  [ctor]
  (fn [state children props]
    (let [comp (ctor)]
      (set-props comp state (dissoc props :action))
      (add-listener comp state (:action props))
      (add-children comp children)
      comp)))

(defn- build-layout
  [ctor]
  (fn [state children props]
    (let [layout-props (:layout props)
	  layout (ctor)
	  props (dissoc props :layout)
	  panel (javax.swing.JPanel.)]
      (set-props layout state layout-props)
      (set-props panel state props)
      (.setLayout panel layout)
      (add-children panel children)
      panel)))

(def components
     {:window (build-standard #(javax.swing.JFrame.))
      :button (build-standard #(javax.swing.JButton.))
      :text (build-standard #(javax.swing.JTextField.))
      :label (build-standard #(javax.swing.JLabel.))
      :grid (build-layout #(java.awt.GridLayout.))
      :vertical (build-standard #(javax.swing.Box/createVerticalBox))
      :horizontal (build-standard #(javax.swing.Box/createHorizontalBox))})

(def getters
     {:text #(.getText %)})

(def setters
     {:title #(.setTitle %1 %2)
      :text #(.setText %1 %2)
      :rows #(.setRows %1 %2)
      :columns #(.setColumns %1 %2)
      :background #(.setBackground %1 (translate/color %2))
      :size #(.setSize %1 %2 %3)
      :resizable? #(.setResizable %1 %2)
      :font #(.setFont %1 (translate/font %2 %3 %4))
      :minimum-size #(.setMinimumSize %1 (Dimension. %2 %3))
      :maximum-size #(.setMaximumSize %1 (Dimension. %2 %3))
      :close-operation #(.setDefaultCloseOperation %1 (translate/close-op %2))})

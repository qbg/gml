(ns gml.widget
  (:import [javax.swing JPanel BoxLayout JButton JLabel JFrame]
	   [java.awt Dimension])
  (:use [gml.manager :only [register-component create-manager]]
	gml.event))

(defn run-extra
  "Run any additional initialization"
  [manager obj options]
  (let [f (or (:setup options) (constantly nil))]
    (f obj)))

(declare construct-gui)

(defn add-children
  "Add gml children to the given object"
  [manager obj children]
  (doseq [c children]
    (if (:expanded (meta c))
      (.add obj (construct-gui manager c))
      (add-children manager obj c))))

(defn vertical*
  "Create a new JPanel with a vertical BoxLayout"
  [manager options & children]
  (let [panel (JPanel.)
	bl (BoxLayout. panel BoxLayout/Y_AXIS)]
    (.setLayout panel bl)
    (register-component manager (:name options) panel)
    (run-extra manager panel options)
    (add-children manager panel children)
    panel))

(defn horizontal*
  "Create a new JPanel with a horizontal BoxLayout"
  [manager options & children]
  (let [panel (JPanel.)
	bl (BoxLayout. panel BoxLayout/X_AXIS)]
    (.setLayout panel bl)
    (register-component manager (:name options) panel)
    (run-extra manager panel options)
    (add-children manager panel children)
    panel))

(defn button*
  "Create a new JButton"
  [manager options text]
  (let [b (JButton. text)
	name (:name options)]
    (.setAlignmentX b 0.5)
    (.setMaximumSize b (Dimension. Short/MAX_VALUE Short/MAX_VALUE))
    (register-component manager name b)
    (register-actionlistener manager b name)
    (run-extra manager b options)
    b))

(defn label*
  "Create a new label"
  [manager options text]
  (let [l (JLabel. text)]
    (.setAlignmentX l 0.5)
    (register-component manager (:name options) l)
    (run-extra manager l options)
    l))

(defn window*
  [manager options & children]
  (let [jframe (JFrame. (or (:title options) ""))]
    (register-component manager (:name options) jframe)
    (run-extra manager jframe options)
    (add-children manager jframe children)
    (doto jframe
      .pack
      .show)
    jframe))

(def widgets
     (atom {:vertical #'vertical*,
	    :horizontal #'horizontal*,
	    :window #'window*,
	    :button #'button*,
	    :label #'label*}))

(defn construct-gui
  "Create a gui in a manager"
  [manager gml]
  (let [[name options & children] gml]
    (-> (or (@widgets name) (constantly nil))
	(apply manager options children))))

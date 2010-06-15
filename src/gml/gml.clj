(ns gml.gml
  (:import [java.awt EventQueue Color Font Dimension]
	   [java.awt.event ActionListener]
	   [javax.swing JPanel JFrame]))

(deftype Gui [model update-list names panel])

(defn gui-model
  "Return the gui's model"
  [gui]
  (.model gui))

(defn- perform-updates
  [gui]
  (let [model @(.model gui)]
    (EventQueue/invokeLater
     #(doseq [f (.update-list gui)]
	(f model)))))

(defn- install-watch
  [gui]
  (let [model (.model gui)]
    (add-watch model ::updater (fn [_ _ _ _] (perform-updates gui)))))

(declare components)
(declare getters)
(declare setters)

(declare ^{:private true} *gui*)
(declare ^{:private true} *update-list*)
(declare ^{:private true} *names*)

(defn- make-component
  [type children props]
  ((components type) children props))
  
(defn- add-children
  [component children]
  (doseq [c children]
    (.add component c)))

(defn- set-prop
  [component prop val]
  (if-let [setter (setters prop)]
    (if (sequential? val)
      (apply setter component val)
      (setter component val))
    (throw (IllegalArgumentException.
	    (format "Property %s not recognized" prop)))))

(defn- get-prop
  [component prop]
  (if-let [getter (getters prop)] 
    (getter component)
    (throw (IllegalArgumentException.
	    (format "Property %s not recognized" prop)))))

(defn get-property
  "From the component named name in gui, get the value corresponding to the prop
property"
  [gui name prop]
  (get-prop ((.names gui) name) prop))

(defn- set-props
  [component props]
  (doseq [[prop val] (seq props)]
    (if (fn? val)
      (swap! *update-list* conj #(set-prop component prop (val %)))
      (set-prop component prop val))))

(defn- add-listener
  [component f]
  (let [gui *gui*
	al
	(reify
	 ActionListener
	 (actionPerformed [_ e]
	   (let [source (.getSource e)]
	     (f @gui))))]
    (if f
      (.addActionListener component al))))

(defn- build-component
  "Takes a gui atom and a form and returns the component"
  [form]
  (let [[type props & children-forms] form
	children (doall (map build-component children-forms))
	component (make-component type children (dissoc props :name))]
    (swap! *names* assoc (:name props) component)
    component))

(defn make-gui
  "Create a gui with a given initial model from the description in form"
  [model form]
  (binding [*gui* (atom nil)
	    *update-list* (atom nil)
	    *names* (atom {})]
    (let [root (build-component form)
	  gui (reset! *gui* (Gui. model @*update-list* @*names* root))]
      (perform-updates gui)
      (install-watch gui)
      (.show root)
      gui)))

(defn- color
  [name]
  (cond
   (keyword? name)
   ({:black Color/black, :blue Color/blue, :cyan Color/cyan, :gray Color/gray,
     :green Color/green, :light-gray Color/lightGray, :magenta Color/magenta,
     :orange Color/orange, :pink Color/pink, :red Color/red, :white Color/white,
     :yell Color/yellow}
    name
    Color/white)
   :else name))

(defn- font
  "Return the font with a given family, style, and size"
  [family style size]
  (let [family
	({:dialog "Dialog", :dialog-input "DialogInput",
	  :monospaced "Monospaced", :serif "Serif", :sans-serif "SansSerif"}
	 family family)
	style
	({:plain Font/PLAIN, :bold Font/BOLD, :italic Font/ITALIC,
	  :bold-italic (bit-or Font/BOLD Font/ITALIC)}
	 style)]
    (Font. family style size)))

(defn- build-standard
  [ctor]
  (fn [children props]
    (let [comp (ctor)]
      (set-props comp (dissoc props :action))
      (add-listener comp (:action props))
      (add-children comp children)
      comp)))

(defn- build-layout
  [ctor]
  (fn [children props]
    (let [layout-props (:layout props)
	  layout (ctor)
	  props (dissoc props :layout)
	  panel (javax.swing.JPanel.)]
      (set-props layout layout-props)
      (set-props panel props)
      (.setLayout panel layout)
      (add-children panel children)
      panel)))

(defn- close-op
  [op]
  ({:nothing JFrame/DO_NOTHING_ON_CLOSE,
    :hide JFrame/HIDE_ON_CLOSE,
    :dispose JFrame/DISPOSE_ON_CLOSE,
    :exit JFrame/EXIT_ON_CLOSE}
   op))

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
      :background #(.setBackground %1 (color %2))
      :size #(.setSize %1 %2 %3)
      :resizable? #(.setResizable %1 %2)
      :font #(.setFont %1 (font %2 %3 %4))
      :minimum-size #(.setMinimumSize %1 (Dimension. %2 %3))
      :maximum-size #(.setMaximumSize %1 (Dimension. %2 %3))
      :close-operation #(.setDefaultCloseOperation %1 (close-op %2))})

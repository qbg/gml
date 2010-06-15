(ns gml.translate
  (:import [java.awt Color Font Dimension]
	   [javax.swing JFrame]))

(defn color
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

(defn font
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

(defn close-op
  [op]
  ({:nothing JFrame/DO_NOTHING_ON_CLOSE,
    :hide JFrame/HIDE_ON_CLOSE,
    :dispose JFrame/DISPOSE_ON_CLOSE,
    :exit JFrame/EXIT_ON_CLOSE}
   op))

;;;; Construct Gui objects
(ns gml.builder
  (:require [gml.swing :as swing]))

(deftype Gui [model update-list names panel])

(defn- perform-updates
  "Update all dynamic properties in gui to reflect its model"
  [gui]
  (let [model @(.model gui)]
    (swing/run-in-gui
     #(doseq [f (.update-list gui)]
	(f model)))))

(defn- install-watch
  [gui]
  (let [model (.model gui)]
    (add-watch model ::updater (fn [_ _ _ _] (perform-updates gui)))))

(defn- build-component
  "Takes a gui atom and a form and returns the component"
  [state form]
  (let [[type props & children-forms] form
	children (doall (map #(build-component state %) children-forms))
	component (swing/make-component
		   state type children (dissoc props :name))]
    (swap! (:names state) assoc (:name props) component)
    component))

(defn make-gui
  "Create a gui with a given model from the description in form"
  [model form]
  (let [state {:gui (atom nil) :update-list (atom nil) :names (atom {})}
	root (build-component state form)
	gui (reset! (:gui state)
		    (Gui. model @(:update-list state) @(:names state) root))]
    (perform-updates gui)
    (install-watch gui)
    (.show root)
    gui))

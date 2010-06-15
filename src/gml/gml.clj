(ns gml.gml
  (:require [gml.swing :as swing]
	    [gml.builder :as builder]))

(defn gui-model
  "Return the gui's model"
  [gui]
  (.model gui))

(defn get-property
  "From the component named name in gui, get the value corresponding to the
property prop"
  [gui name prop]
  (swing/get-prop ((.names gui) name) prop))

(defn make-gui
  "Create a gui with a given model from the description in form"
  [model form]
  (builder/make-gui model form))


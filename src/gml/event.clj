(ns gml.event
  (:import [java.awt.event ActionListener])
  (:use [gml.manager :only [invoke-event]]))

(defn register-actionlistener
  "Add an action listener on the given object with name"
  [manager obj name]
  (let [al (reify
	    ActionListener
	    (actionPerformed [this e]
	     (invoke-event manager name [:action])))]
    (.addActionListener obj al)))

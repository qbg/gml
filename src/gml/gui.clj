(ns gml.gui
  (:use gml.manager
	[clojure.template :only [do-template]]))

(defn realize-gml
  "Create a gui from some given gml"
  [gml]
  (let [manager (create-manager)]
    (construct-gui manager gml)
    manager))

(defmacro create-gui
  "Create a gui from a template"
  [gml & events]
  `(doto (realize-gml ~gml)
     ~@(for [[[name & type] f & args] events]
	 `(register-event ~name ~(vec type) ~f ~@args))))

(defn by-name
  "Get a component from the current gui context by name"
  [name]
  (gml.manager/find-component gml.manager/*manager* name))

(do-template [name]
	     (let [element (keyword 'name)]
	       (defn name [& args]
		 (if (map? (first args))
		   (with-meta (list* element args) {:expanded true})
		   (with-meta (list* element {} args) {:expanded true}))))
  vertical horizontal button label window)

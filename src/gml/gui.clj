(ns gml.gui
  (:use gml.manager
	[gml.widget :as w]
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
     ~@(for [[name type f & args] events]
	 `(register-event ~name ~type ~f ~@args))))

(do-template [f c]
	     (defn f [& args]
	       (if (map? (first args))
		 (with-meta (list* c args) {:expanded true})
		 (with-meta (list* c {} args) {:expanded true})))
  vertical w/vertical*
  horizontal w/horizontal*
  button w/button*
  label w/label*
  window w/window*)

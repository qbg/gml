(ns gml.manager)

(def *manager* nil)

(defn create-manager
  "Create a new gui manager backed by a JPanel"
  []
  (atom {:names {}, :events {}}))

(defn register-component
  "Add the component to the list of known components in manager"
  [manager name component]
  (if name
    (swap! manager assoc-in [:names name] component)))

(defn find-component
  "Return the component with the specified name, or nil"
  [manager name]
  (get-in @manager [:names name]))

(defn register-event
  "Set f to be the event handler for event-type on the component named name,
with any additional args"
  ([manager name [& event-type] f]
     (swap! manager assoc-in (list* name event-type) f))
  ([manager name [& event-type] f & args]
     (register-event manager name event-type (apply partial f args))))

(defn unregister-event
  "Remove the event handler for event-type on the on the component named name"
  [manager [& event-type]]
  (swap! manager #(apply dissoc % event-type)))

(defn invoke-event
  "Call the event handler for the event with any additional arguments"
  [manager name [& event-type] & args]
  (let [f (or (get-in @manager (list* name event-type))
	      (constantly nil))]
    (binding [*manager* manager]
      (apply f args))))

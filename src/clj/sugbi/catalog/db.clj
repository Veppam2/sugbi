(ns sugbi.catalog.db
 (:require
  [camel-snake-kebab.core :as csk]
  [clojure.string :as str]
  [conman.core :as conman]
  [sugbi.db.core :as db]
  [medley.core :as medley]))

(conman/bind-connection db/*db* "sql/catalog.sql")

(defn matching-books
  [title]
  (map
   #(medley/map-keys csk/->kebab-case %)
   (search {:title (str "%" (str/lower-case title) "%")})))


(defn book-in-loan [item-id]
  (if (book-is-in-loan-table {:loan-id item-id}) true false)
)

(defn check-if-item-exists [item-id]
  (item-exists {:book-item-id item-id})
)

(defn item-available [item-id]
  (item-is-available {:book-item-id item-id})
)

(defn checkout-book [user-id book-item-id]
     (deactivate-item! {:item-id book-item-id})
     (insert-loan! {:item-id book-item-id
                    :user-id user-id})
)

;;(checkout-book  1 1 )
;;(get-loans-all)
;;(get-books-all)
;;(get-items-all)
;;(return-book  1 1 )
;;(activate-item! {:item-id 1})
;;(delete-loan! {:item-id 1 :user-id (str 1)  } )

(defn loan-exists [user-id book-item-id]
  (get-loan {:user-id user-id :item-id book-item-id } )
)

(defn return-book [user-id book-item-id]
  (activate-item! {:item-id book-item-id})
  (delete-loan! {:item-id book-item-id :user-id (str user-id) } )
)

(defn get-book-lendings [user-id]
  (get-user-book-loans {:user-id user-id})
)

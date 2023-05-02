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


;;User asks for a book
(defn book-in-loan [item-id]
  (if (book-is-in-loan-table {:loan-id item-id}) true false)
)

(defn checkout-book [user-id book-item-id]
  (if (book-in-loan book-item-id)
    (
     (insert-loan! {:item-id book-item-id
                    :user-id user-id}
     )
     (deactivate-item! {:item-id book-item-id}
     )
    )
    "El ejemplar no está disponible"
  )
)

;;(checkout-book 20 20)

;;Returning a book
(defn return-book [user-id book-item-id]
  (delete-loan! {:item-id book-item-id :user-id user-id } )
  (activate-item! {:item-id book-item-id})
)

;;(return-book 12 12)

;;Get books that a user has
(defn get-book-lendings [user-id]
  (get-user-book-loans {:user-id user-id})
)

;;(get-book-lendings 20)

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
     (delete-item! {:item-id book-item-id
                    :user-id user-id}
     )
    )
    "El ejemplar no está disponible"
  )
)

;;Returning a book
(defn return-book [user-id book-item-id]
  (delete-loan! {:item-id book-item-id :user-id user-id } )
  (insert-item! {:item-id book-item-id :user-id user-id } )
)

;;Get books that a user has
(defn get-book-lendings [user-id]
  (get-user-book-loans {:user-id user-id})
)

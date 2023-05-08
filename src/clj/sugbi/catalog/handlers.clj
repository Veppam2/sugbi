(ns sugbi.catalog.handlers
  (:require
   [ring.util.http-response :as response]
   [sugbi.catalog.db :as catalog.db]
   [sugbi.catalog.core :as catalog.core]
   [sugbi.auth.google.handlers :as google.handlers]
  )
)


(defn librarian-get-user-loans [request]
  (let [{:keys [_user-id] :as user-info} (get-in request [:parameters :body])
        is-in-session? (get-in request [:session])
        is-in-librarian-session?   (get-in request [:session :is-librarian?])
       ]
       (if-let [ the-session (get-in request [:session]) ]
            (if-let [librarian-session (get-in request [:session :is-librarian?])]
                    ()
                    (response/forbidden (:message "User must be an admin"))
            )
            (response/forbidden (:message "User must have started session"))
       )
  )
)

(defn get-user-loans [request]
  (let [user-id (get-in google.handlers/callback-data [:user-info :sub])
        session   (get-in request [:session ])
       ]
       (if (some? session)
           (response/ok  ( select-keys (catalog.db/get-user-book-loans {:user-id user-id} )
                                       [:item_id :loan_date :return_date]
                         )
        )
           (response/forbidden (:message "User is not logged in"))
       )
  )
)

(defn remove-loan! [request]
  (let [user-id (get-in request [:parameters :path :user-id])
        book-item-id (get-in request [:parameters :path :book-item-id] )
        session   (get-in request [:session ])
       ]
       (if (some? session)
        (if-let [item-info (catalog.db/item-exists book-item-id ) ]
          (if-let [item-info (catalog.db/loan-exists user-id book-item-id ) ]
            (response/ok (select-keys (catalog.db/return-book user-id book-item-id )
                                      [:item_id :loan_date :return_date]
                         )
            )
            (response/conflict (:message "loan is not linked to current user"))
          )
          (response/not-found {:message "book-item-id not found"})
        )
        (response/forbidden {:message "User is not logged in"})
      )
  )
)

(defn insert-loan! [request]
  (let [user-id (get-in google.handlers/callback-data [:user-info :sub])
        book-item-id (get-in request [:parameters :path :book-item-id] )
        session   (get-in request [:session ])
       ]
       (if ( some? session )
        (if-let [item-info (catalog.db/check-if-item-exists book-item-id) ]
          (if-let [loan-info (catalog.db/loan-exists user-id book-item-id  ) ]
            (response/conflict (:message "book-item is already taken"))
            (response/ok  (select-keys (catalog.db/checkout-book user-id book-item-id)
                                       [:item_id :loan_date :return_date]
                          )
            )
          )
          (response/not-found {:message "book-item-id not found"})
        )
        (response/forbidden {:message "User is not logged in"})
       )
   )
)


(defn search-books
  [request]
  (if-let [criteria (get-in request [:parameters :query :q])]
    (response/ok
     (catalog.core/enriched-search-books-by-title
      criteria
      catalog.core/available-fields))
    (response/ok
     (catalog.core/get-books
      catalog.core/available-fields))))


(defn insert-book!
  [request]
  (let [{:keys [_isbn _title]
         :as book-info} (get-in request [:parameters :body])
        is-librarian?   (get-in request [:session :is-librarian?])]
    (if is-librarian?
      (response/ok
       (select-keys (catalog.db/insert-book! book-info) [:isbn :title]))
      (response/forbidden {:message "Operation restricted to librarians"}))))


(defn delete-book!
  [request]
  (let [isbn          (get-in request [:parameters :path :isbn])
        is-librarian? (get-in request [:session :is-librarian?])]
    (if is-librarian?
      (response/ok
       {:deleted (catalog.db/delete-book! {:isbn isbn})})
      (response/forbidden {:message "Operation restricted to librarians"}))))


(defn get-book
  [request]
  (let [isbn (get-in request [:parameters :path :isbn])]
    (if-let [book-info (catalog.core/get-book
                        isbn
                        catalog.core/available-fields)]
      (response/ok book-info)
      (response/not-found {:isbn isbn}))))

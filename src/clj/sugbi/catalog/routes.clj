(ns sugbi.catalog.routes
  (:require
   [spec-tools.data-spec :as ds]
   [sugbi.catalog.handlers :as catalog.handlers]))

(defn date? [o]
  (instance? java.time.LocalDate o)
)

(def loan-basic-info
  {:item-id int?
   :start-loan date?
   :end-loan date?
  }
)

(def loan-full-info
  {
   :isbn string?
   :title string?
   :start-loan string?
   :end-loan string?
  }
)

(def basic-book-info-spec
  {:isbn  string?
   :title string?})


(def book-info-spec
  {:isbn                         string?
   :available                    boolean?
   (ds/opt :title)               string?
   (ds/opt :full-title)          string?
   (ds/opt :subtitle)            string?
   (ds/opt :publishers)          [string?]
   (ds/opt :publish-date)        string?
   (ds/opt :weight)              string?
   (ds/opt :physical-dimensions) string?
   (ds/opt :genre)               string?
   (ds/opt :subjects)            [string?]
   (ds/opt :number-of-pages)     int?})


(def routes
  ["/catalog" {:swagger {:tags ["Catalog"]}}
   ["/books"
    ["" {:get  {:summary    "gets the catalog. Optionally, accepts a search criteria"
                :parameters {:query {(ds/opt :q) string?}}
                :responses  {200 {:body [book-info-spec]}}
                :handler    catalog.handlers/search-books}
         :post {:summary    "add a book title to the catalog"
                :parameters {:header {:cookie string?}
                             :body   basic-book-info-spec}
                :responses  {200 {:body basic-book-info-spec}
                             405 {:body {:message string?}}}
                :handler    catalog.handlers/insert-book!}}]
    ["/:isbn"
        ["" {:get    {:summary    "get a book info by its isbn"
                        :parameters {:path {:isbn string?}}
                        :responses  {200 {:body book-info-spec}
                                     404 {:body {:isbn string?}}}
                        :handler    catalog.handlers/get-book
                     }
               :delete {:summary    "delete a book title of the catalog"
                        :parameters {:header {:cookie string?}
                                     :path   {:isbn string?}}
                        :responses  {200 {:body {:deleted int?}}
                                     405 {:body {:message string?}}}
                        :handler    catalog.handlers/delete-book!
                       }
             }
        ]
        ["/item"
            ["/:book-item-id"
                ["/checkout" {:post  {:summary "current session user ask for a book in :book-item-id to be lend"
                                     :parameters { :path {:book-item-id int?
                                                          :isbn string?
                                                         }
                                                 }
                                     :responses  {200 {:body loan-basic-info}
                                                  404 {:body {:message string?}}
                                                  409 {:body {:message string?}}
                                                  403 {:body {:message string?}}
                                                 }
                                     :handler    catalog.handlers/insert-loan!
                                     }
                              }
                ]
                ["/return" {:post  { :summary "current session's user returns book-item"
                                     :parameters {:header {:cookie string? }
                                                  :query {(ds/opt :q) string?}
                                                 }
                                     :responses  {200 {:body {:loan-info loan-basic-info}}
                                                  403 {:body {:message string?}}
                                                  404 {:body {:message string?}}
                                                  409 {:body {:message string?}}
                                                 }
                                     :handler  catalog.handlers/remove-loan!
                                   }
                            }
                 ]
             ]
         ]
     ]
    ]
    ["/user" {:swagger {:tags ["User"]}}
        ["/lendings" {  :get {:summary  "User sees all his lend books"
                        :parameters {:header {:cookie string?}
                                    }
                        :responses  {200 {:body [loan-basic-info] }
                                     403 {:body {:message string?}}
                                    }
                        :handler    catalog.handlers/get-user-loans
                        }
                     }
        ]
    ]
    ["/lendings" {:swagger {:tags ["Librarian"]}}
        ["?user-id=:user-id" {  :get {:summary    "Librarian sees user loan books"
                                       :parameters {:header {:cookie string?}
                                                    :path {:user-id pos-int?}
                                                   }
                                       :responses  {200 {:body {:loans-list [loan-basic-info] }}
                                                    405 {:body {:message string?} }
                                                   }
                                       :handler    catalog.handlers/get-user-loans
                                       }
                               }
        ]
    ]
  ]
)

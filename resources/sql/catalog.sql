-- :name insert-book! :! :1
insert into catalog.book (title, isbn) values (:title, :isbn)
returning *;

-- :name delete-book! :! :n
delete from catalog.book where isbn = :isbn;

-- :name search :? :*
select isbn, true as "available"
from catalog.book
where lower(title) like :title;

-- :name get-book :? :1
select isbn, true as "available"
from catalog.book
where isbn = :isbn

-- :name get-books :? :*
select isbn, true as "available"
from catalog.book;

-- :name get-books-all :? :*
select * from catalog.book;

-- :name insert-librarian :? :1
insert into user_management.librarian (sub) values (:sub)
returning *;

-- :name book-is-in-loan-table :? :1
select loan_id from catalog.loan
where loan_id = :loan-id;

-- :name item-exists :? :1
select * from catalog.item
where item_id = :book-item-id;

-- :name item-is-available :? :1
select * from catalog.item
where item_id = :book-item.id
and status = true;

-- :name insert-loan! :! :1
insert into catalog.loan
(item_id, user_id, loan_date, return_date) values
(:item-id, :user-id, current_date, current_date -interval* '2 week')
returning *;

-- :name insert-item! :! :1
insert into catalog.item
( book_id, state) values
( :book-id,true)
returning *;

--:name get-items-all :? :*
select * from catalog.item;

-- :name activate-item! :! :1
update catalog.item
set status = true
where item_id = :item-id;

-- :name deactivate-item! :? :*
update catalog.item
set status = false
where item_id = :item-id;

-- :name get-loan :? :*
select * from catalog.loan
where user_id = :user-id
and item_id = :item_id;

-- :name delete-loan! :? :*
delete from catalog.loan
where item_id = :item-id and user_id = :user-id;

-- :name get-user-book-loans :? :*
select * from
catalog.loan natural join catalog.item natural join catalog.book
where user_id = :user-id;

-- :name get-number-of-total-item-books :? :1
select count(*) from
catalog.book natural join catalog.item
where isbn = :isbn;

-- :name get-number-of-total-lend-books :? :1
select count(*) from
catalog.book natural join catalog.loan
where isbn = :isbn;

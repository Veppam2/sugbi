create table catalog.item(
   item_id bigint generated always as identity primary key,
   book_id bigint not null references catalog.book(book_id)
);
--;;
create table catalog.loan(
   loan_id bigint generated always as identity primary key,
   item_id bigint not null references catalog.item(item_id),
   user_id bigint not null,
   loan_date date not null,
   return_date date not null
);

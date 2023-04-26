drop table user_management.loans;
--;;
drop table user_management.member;
--;;
drop table user_management.librarian;
--;;
drop table catalog.book;
--;;
create table catalog.book (
  book_id bigint generated always as identity primary key,
  title text not null unique
);
--;;
alter table catalog.book
 add isbn text not null unique;
--;;
create table user_management.librarian (
  librarian_id bigint generated always as identity primary key,
  sub text not null unique
);

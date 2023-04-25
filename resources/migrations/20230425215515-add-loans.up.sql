drop table catalog.book;
--;;
create table catalog.book (
  book_id bigint generated always as identity primary key,
  title text not null,
  status smallint not null
);
--;;
drop table user_management.librarian;
--;;
create table user_management.librarian (
  librarian_id bigint generated always as identity primary key,
  sub text not null unique,
  full_name text not null,
  email text not null,
  password text not null
);
--;;
create table user_management.member (
  member_id bigint generated always as identity primary key,
  full_name text not null,
  email text not null,
  password text not null,
  status smallint not null
);
--;;
create table user_management.loans (
  book_id bigint not null references catalog.book,
  member_id bigint not null references user_management.member,
  loan_date date not null,
  loan_return date not null
);

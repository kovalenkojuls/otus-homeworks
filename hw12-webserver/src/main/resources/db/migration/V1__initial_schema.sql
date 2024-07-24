create sequence client_SEQ start with 1 increment by 1;

create table clients
(
    id   bigint not null primary key,
    address_id bigint,
    name varchar(50)
);

create table addresses
(
    id   bigserial not null primary key,
    street varchar(50)
);

create table phones
(
    id   bigserial not null primary key,
    number varchar(50),
    client_id bigint not null
);

create table users
(
    id   bigserial not null primary key,
    login varchar(50) unique,
    password varchar(50)
);

insert into users values (1, 'admin', 'admin');
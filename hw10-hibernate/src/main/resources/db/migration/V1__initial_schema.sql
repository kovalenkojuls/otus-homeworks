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

create table clients
(
    id   bigserial not null primary key,
    name varchar(50)
);

create table addresses
(
    street varchar(50),
    client_id bigint not null
);

create table phones
(
    id   bigserial not null primary key,
    number varchar(50),
    client_id bigint not null
);

-- Test data:
insert into clients (name)
values ('Andrey'),
       ('Dmitriy');

insert into addresses (client_id, street)
values (1, 'Arbat Street'),
       (2, 'Lenin Street');

insert into phones (number, client_id)
values ('89160001122', 1),
       ('89163334455', 1),
       ('89166667788', 1),
       ('89169998877', 2),
       ('89166665544', 2);

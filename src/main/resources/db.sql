-- auto-generated definition
create schema rewards;

alter schema rewards owner to postgres;

create table rewards.customer
(
    id   serial
        constraint customer_pk
            primary key,
    name varchar(250)
);

alter table rewards.customer
    owner to postgres;

create unique index customer_id_uindex
    on rewards.customer (id);

create table rewards.transaction
(
    id               serial
        constraint transaction_pk
            primary key,
    type             varchar(256),
    amount           money,
    customer_id      integer,
    transaction_date timestamp default CURRENT_TIMESTAMP
);

alter table rewards.transaction
    owner to postgres;

create unique index transaction_id_uindex
    on rewards.transaction (id);




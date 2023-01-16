-- City table
create table city
(
    id            bigserial
        primary key,
    official_name varchar(255),
    area          bigint,
    population    bigint,
    time_zone     smallint
);

alter table city
    owner to postgres;

-- Resident table
create table resident
(
    id            bigserial
        primary key,
    date_of_birth date,
    first_name    varchar(255),
    last_name     varchar(255),
    id_city       bigint
        constraint fk9pl433elo11b4re5a2mx01br6 -- Random constraint
            references city
);

alter table city
    owner to postgres;
--liquibase formatted sql

--changeset vLisin:init_schema
create table USERS
(
    ID         INTEGER auto_increment primary key,
    NAME       VARCHAR(100)            not null,
    EMAIL      VARCHAR(100)            not null constraint USERS_UK unique,
    ENABLED    BOOL      default TRUE  not null,
    PASSWORD   VARCHAR(100)            not null,
    REGISTERED TIMESTAMP default NOW() not null
);

create table USER_ROLES
(
    USER_ID INTEGER not null,
    ROLE    VARCHAR(255),
    constraint USER_ROLES_UI unique (USER_ID, ROLE),
    constraint USER_ROLES_FK foreign key (USER_ID) references USERS (ID) on delete cascade
);

create table RESTAURANT
(
    ID      INTEGER auto_increment primary key,
    NAME    VARCHAR(100)      not null,
    ADDRESS VARCHAR(1024),
    ENABLED BOOL default TRUE not null,
    constraint RESTAURANTS_UK unique (NAME, ADDRESS)
);

create table DISH_REF
(
    ID            INTEGER auto_increment primary key,
    NAME          VARCHAR(100)      not null,
    ENABLED       BOOL default TRUE not null,
    PRICE         INTEGER           not null,
    RESTAURANT_ID INTEGER,
    constraint DISH_REF_UK unique (RESTAURANT_ID, NAME),
    constraint DISH_REF_RESTAURANT_FK foreign key (RESTAURANT_ID) references RESTAURANT (ID) on delete cascade
);

create table MENU_ITEM
(
    ID            INTEGER auto_increment primary key,
    ACTUAL_DATE   DATE not null,
    DISH_REF_ID   INTEGER,
    RESTAURANT_ID INTEGER,
    constraint MENU_ITEM_UK unique (ACTUAL_DATE, DISH_REF_ID),
    constraint MENU_ITEM_RESTAURANT_FK foreign key (RESTAURANT_ID) references RESTAURANT (ID) on delete cascade,
    constraint MENU_ITEM_DISH_REF_FK foreign key (DISH_REF_ID) references DISH_REF (ID)
);

create table VOTE
(
    ID            INTEGER auto_increment primary key,
    ACTUAL_DATE   DATE    not null,
    ACTUAL_TIME   TIME    not null,
    RESTAURANT_ID INTEGER,
    USER_ID       INTEGER not null,
    constraint VOTE_UK unique (USER_ID, ACTUAL_DATE),
    constraint VOTE_USER_FK foreign key (USER_ID) references USERS (ID) on delete cascade,
    constraint VOTE_RESTAURANT_FK foreign key (RESTAURANT_ID) references RESTAURANT (ID)
);

--changeset vLisin:populate_data
INSERT INTO USERS (NAME, EMAIL, PASSWORD)
VALUES ('User', 'user@yandex.ru', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin');

INSERT INTO USER_ROLES (ROLE, USER_ID)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2);

INSERT INTO RESTAURANT (NAME, ADDRESS, ENABLED)
VALUES ('Якитория', 'Балаклавский просп., 14А', true),
       ('Додо Пицца', 'ул. Намёткина, 13Б', true),
       ('McDonalds', 'Газетный пер., 17', false),
       ('Теремок', 'Гоголевский бул., 3', true),
       ('Starbucks', 'ул. Арбат, 19', false);

INSERT INTO DISH_REF (NAME, PRICE, RESTAURANT_ID, ENABLED)
VALUES ('Калифорния', 55700, 1, true),
       ('Ролл Лосось-карамель', 49900, 1, true),

       ('Карбонара', 71900, 2, true),
       ('Пепперони', 64900, 2, true),

       ('Картофель Фри', 12800, 3, true),
       ('Филе-о-Фиш', 12700, 3, false),
       ('Двойной Чизбургер', 12500, 3, true),
       ('Чикенбургер', 5300, 3, true),

       ('Борщ с уткой', 27600, 4, true),
       ('Блин Карбонара', 31800, 4, true),
       ('Блин Сгущёнка', 22100, 4, true),

       ('Капучино Шорт', 30000, 5, true),
       ('Эспрессо Доппио', 15500, 5, false),
       ('Американо Кон Крема', 39000, 5, true);

INSERT INTO MENU_ITEM (ACTUAL_DATE, RESTAURANT_ID, DISH_REF_ID)
VALUES
--- CURRENT_DATE
(CURRENT_DATE, 1, 1),
(CURRENT_DATE, 1, 2),

(CURRENT_DATE, 2, 3),
(CURRENT_DATE, 2, 4),

(CURRENT_DATE, 3, 5),
(CURRENT_DATE, 3, 6),
(CURRENT_DATE, 3, 7),
(CURRENT_DATE, 3, 8),

(CURRENT_DATE, 4, 9),
(CURRENT_DATE, 4, 10),
(CURRENT_DATE, 4, 11),

(CURRENT_DATE, 5, 12),
(CURRENT_DATE, 5, 13),
(CURRENT_DATE, 5, 14),

--- 2022-04-14
('2022-04-14', 1, 1),
('2022-04-14', 1, 2),

('2022-04-14', 2, 3),
('2022-04-14', 2, 4),

('2022-04-14', 4, 9),
('2022-04-14', 4, 10),
('2022-04-14', 4, 11),

--- 2022-04-13
('2022-04-13', 1, 1),
('2022-04-13', 1, 2),

('2022-04-13', 2, 3),
('2022-04-13', 2, 4),

('2022-04-13', 3, 5),
('2022-04-13', 3, 6),
('2022-04-13', 3, 7),
('2022-04-13', 3, 8),

('2022-04-13', 4, 9),
('2022-04-13', 4, 10),
('2022-04-13', 4, 11),

('2022-04-13', 5, 12),
('2022-04-13', 5, 13),
('2022-04-13', 5, 14);


INSERT INTO VOTE (USER_ID, ACTUAL_DATE, ACTUAL_TIME, RESTAURANT_ID)
VALUES (1, CURRENT_DATE, '12:35:00', 4),
       (1, '2022-04-14', '09:13:00', 2),
       (2, '2022-04-14', '08:25:00', 2),
       (1, '2022-04-13', '14:55:00', 3),
       (2, '2022-04-13', '12:55:00', 5);

--changeset vLisin:add_admin_restaurant
create table ADMIN_RESTAURANT
(
    ADMIN_ID      INTEGER not null,
    RESTAURANT_ID INTEGER,
    primary key (ADMIN_ID, RESTAURANT_ID),
    constraint ADMIN_RESTAURANT_ADMIN_FK foreign key (ADMIN_ID) references USERS (ID) on delete cascade,
    constraint ADMIN_RESTAURANT_RESTAURANT_FK foreign key (RESTAURANT_ID) references RESTAURANT (ID) on delete cascade
);

INSERT INTO USERS (name, email, password) VALUES ('Restaurants Admin', 'r_admin@gmail.com', '{noop}radmin');
INSERT INTO USER_ROLES (role, user_id) VALUES ('R_ADMIN', 3);
INSERT INTO ADMIN_RESTAURANT (ADMIN_ID, RESTAURANT_ID) VALUES (3, 2),(3, 4);
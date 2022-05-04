INSERT INTO USERS (NAME, EMAIL, PASSWORD)
VALUES ('User', 'user@yandex.ru', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin'),
       ('Restaurants Admin', 'r_admin@gmail.com', '{noop}radmin');

INSERT INTO USER_ROLES (ROLE, USER_ID)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2),
       ('R_ADMIN', 3);

INSERT INTO ADMIN_RESTAURANT (ADMIN_ID, RESTAURANT_ID)
VALUES (3, 2),
       (3, 4);

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
**RU** | [EN](README_EN.md)

Менеджер обеда
===============================

##### Выпускной проект стажировки [Java Online Projects](https://javaops.ru/view/topjava)
- Исходный код взят из **TopJava**;
- **Менеджер обеда** выполнялся на основе этого репозитория;
- Запустить в корневом каталоге: **mvn spring-boot:run**.

-----

## Задание
Разработать и внедрить JSON API с помощью Spring Data JPA / Hibernate / Spring MVC (или Spring Boot).

> **Build a voting system for deciding where to have lunch.**
>* 2 types of users: admin and regular users
>* Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
>* Menu changes each day (admins do the updates)
>* Users can vote on which restaurant they want to have lunch at
>* Only one vote counted per user
>* If user votes again the same day:
>   - If it is before 11:00 we assume that he changed his mind.
>   - If it is after 11:00 then it is too late, vote can't be changed
>
>Each restaurant provides new menu each day.
>
>As a result, provide a link to github repository. It should contain the code, README.md with API documentation and couple curl commands to test it.

P.S.: Предположим, что API будет использоваться разработчиком внешнего интерфейса для создания внешнего интерфейса
поверх этого.

-----
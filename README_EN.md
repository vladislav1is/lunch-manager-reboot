**EN** | [RU](README.md)

Lunch manager
===============================

##### Graduation internship project of [Java Online Projects](https://javaops.ru/view/topjava)
- Source code taken from **TopJava**;
- **Lunch manager** was based on this repository;
- Run in root directory: **mvn spring-boot:run**.

-----

## The task is
Design and implement a JSON API using  Spring Data JPA / Hibernate / Spring MVC (or Spring Boot).

>**Build a voting system for deciding where to have lunch.**
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

P.S.: Assume that your API will be used by a frontend developer to build frontend on top of that.

-----
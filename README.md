# Getting Started

### Problem Statement

A retailer offers a rewards program to its customers,
awarding points based on each recorded purchase.

A customer receives 2 points for every dollar spent over $100 in each transaction,
plus 1 point for every dollar spent over $50 in each transaction
(e.g. a $120 purchase = 2x$20 + 1x$50 = 90 points).

### Solution

This is a spring boot application exposing endpoints for the following:

* Customer (CRUD)           /api/customer
* Transaction (CRUD)        /api/transaction
* Rewards (Business Logic)  /api/rewards/quarterly/customer/:id

### Installation

* Build the project with maven
    - mvn clean build test
* Install the latest version of PostgresSQL server locally.
* Install the database schema using the db.sql file located under src/main/resources
* Datasource is setup to point to localhost with a username of postgres and a password of postgres

### Running the application

* Start the application as a standard spring boot project
    - ./mvnw spring-boot:run
* Use the included insomnia import file under src/main/resources to run endpoints locally

### For further reference, please consider the following sections:

* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.1/maven-plugin/reference/html/)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#using.devtools)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#appendix.configuration-metadata.annotation-processor)
* [Rest Repositories](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#howto.data-access.exposing-spring-data-repositories-as-rest)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#web)

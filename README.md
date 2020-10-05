
# Relink

A URL shortener served as a web application

You can find the live site at **[relink.r-vu.net](https://relink.r-vu.net)**

## Setup & Usage

 1. Download a copy of the repository and change directory to the folder

    ```bash
    git clone https://github.com/r-vu/relink.git
    cd relink
    ```

 2. **(Optional)** Modify the database credentials and connection in `src/main/resources/application.properties` through the following properties:

    ```
    spring.jpa.hibernate.ddl-auto
    spring.datasource.url
    spring.datasource.username
    spring.datasource.password
    ```

    By default, these properties have been commented out, so the application will default to using an in-memory H2 database, which will have its data be deleted once the application terminates. If you wish to setup your own database connection, you will need to supply the correct URL and credentials for it, as well as the necessary driver if you plan to use something other than PostgreSQL. Other properties can be [found here](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html).

 3. **(Optional)** Add OAuth2 credentials in `src/main/resources/application.properties`. By default the application has properties for Github and Google, but other providers can be used.

    ```
    spring.security.oauth2.client.registration.{PROVIDER}.client-id
    spring.security.oauth2.client.registration.{PROVIDER}.client-secret
    spring.security.oauth2.client.registration.{PROVIDER}.scope
    ```

 4. Run the application

    ```bash
    ./mvnw spring-boot:run
    ```

 5. Access the site at <http://localhost:8080>

By default, a site user account with username **`relinkadmin`** and password **`relinkpassword`** is created for use if not already present.

## Technologies Used

- RESTful API written in Java with Spring
- OAuth2 and local user accounts
- Data is stored in a PostgreSQL database, exposed using JPA
- Page templating handled using Thymeleaf
- User interface designed using React and Bootstrap
- Live site deployed using AWS EC2 & RDS

## Challenges Met

### The JPQL Null Value Skip Problem

When first restricting ShortURLs to only be accessible by their owners, my original JPQL query worked without problem. Owners could only see ShortURLs they created and that was it.

```Java
"SELECT s FROM ShortURL s WHERE s.owner.name = ?#{authentication?.name}"
```

Next, I wanted to implement an override for accounts with admin privileges, allowing them to view all ShortURLs regardless of owner. Initially I tried this JPQL query:

```Java
"SELECT s FROM ShortURL s WHERE (1=?#{hasRole('ROLE_ADMIN') ? 1 : 0} " +
"OR s.owner.name = ?#{authentication?.name})"
```

However, in this query, any ShortURLs with `null` for owner would be skipped and become unviewable, despite the other `OR` condition passing for admin accounts. To solve this, a `LEFT JOIN` must be used:

```Java
"SELECT s from ShortURL s " +
"LEFT JOIN s.owner owner WHERE (1=?#{hasRole('ROLE_ADMIN') ? 1 : " +
"0} OR (owner IS NOT NULL AND owner.name = ?#{authentication?.name}))"
```

Using this query, ShortURLs with `null` owners are considered correctly. An alternative approach could be to assign by default a dummy account to any ShortURL that is created anonymously, removing the possibility of `null` for an owner.

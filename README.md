
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

 3. Run the application

    ```bash
    ./mvnw spring-boot:run
    ```

 4. Access the site at <http://localhost:8080>

By default, a site user account with username **`admin`** and password **`password`** is created for use if not already present.

## Technologies Used

- RESTful API written in Java with Spring
- Data is stored in a PostgreSQL database, exposed using JPA
- Page templating handled using Thymeleaf
- User interface designed using React and Bootstrap
- Live site deployed using AWS EC2 & RDS

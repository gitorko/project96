# GraphQL

[https://gitorko.github.io/](https://gitorko.github.io/)

Spring Boot & GraphQL

## Setup

You need java 17 installed

### Postgres DB

```bash
docker run -p 5432:5432 --name pg-container -e POSTGRES_PASSWORD=password -d postgres:9.6.10
docker ps
docker run -it --rm --link pg-container:postgres postgres psql -h postgres -U postgres
CREATE USER test WITH PASSWORD 'test@123';
CREATE DATABASE "test-db" WITH OWNER "test" ENCODING UTF8 TEMPLATE template0;
grant all PRIVILEGES ON DATABASE "test-db" to test;
```

If container already exists then start it

```bash
docker start pg-container
```

### Dev

```bash
cd project96
./gradlew bootRun
```

### Prod
To run as a single jar, both UI and backend are bundled to single uber jar.

```bash
./gradlew bootJar
cd project96/build/libs
java -jar project96-1.0.0.jar
```

### Graph IQL
[http://localhost:8080/graphiql](http://localhost:8080/graphiql)


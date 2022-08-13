# GraphQL

[https://gitorko.github.io/](https://gitorko.github.io/)

Spring Boot & GraphQL

### Version

Check version

```bash
$java --version
openjdk 17.0.3 2022-04-19 LTS

node --version
v16.16.0

yarn --version
1.22.18
```

### Postgres DB

```
docker run -p 5432:5432 --name pg-container -e POSTGRES_PASSWORD=password -d postgres:9.6.10
docker ps
docker exec -it pg-container psql -U postgres -W postgres
CREATE USER test WITH PASSWORD 'test@123';
CREATE DATABASE "test-db" WITH OWNER "test" ENCODING UTF8 TEMPLATE template0;
grant all PRIVILEGES ON DATABASE "test-db" to test;

docker stop pg-container
docker start pg-container
```

### Dev

To run the backend in dev mode.
Postgres DB is needed to run the integration tests during build.

```bash
./gradlew clean build
./gradlew bootRun
```

### Prod

To run as a single jar.

```bash
./gradlew bootJar
cd project96/build/libs
java -jar project96-1.0.0.jar
```

### Graph IQL

GraphQL comes with a browser client to test the Query. This can be enabled in properties

```yaml
graphql.graphiql.enabled: true
```

Open [http://localhost:8080/graphiql](http://localhost:8080/graphiql)

### Postman

Import the postman collection to postman

[Postman Collection](https://github.com/gitorko/project96/blob/main/postman/Project96.postman_collection.json)

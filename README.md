## How to start

#### Docker
```bash
docker-compose up -d --build
```
#### Local
###### 1. Set up PostgreSQL database
###### 2. Set ENV variables:
```
export SPRING_DATASOURCE_URL=???
export SPRING_DATASOURCE_USERNAME=???
export SPRING_DATASOURCE_PASSWORD=???
```
example:
```
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/library
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
```
###### 3. Run app
```bash 
make run
```
or 
```bash 
mvn spring-boot:run
```

FULL COMMAND
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/library && \
export SPRING_DATASOURCE_USERNAME=postgres && \
export SPRING_DATASOURCE_PASSWORD=postgres && \
make run
```
or 
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/library && \
export SPRING_DATASOURCE_USERNAME=postgres && \
export SPRING_DATASOURCE_PASSWORD=postgres && \
mvn spring-boot:run
```

#### Swagger
```
http://localhost:8080/swagger-ui/index.html
```
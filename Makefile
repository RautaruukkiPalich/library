.PHONY: run

run:
	mvn spring-boot:run

build:
	mvn clean package

clean:
	mvn clean

test:
	mvn test

dev-up:
	docker-compose -f docker-compose.dev.yaml up -d --build

dev-down:
	docker-compose -f docker-compose.dev.yaml down

dev-shell: 
	docker-compose -f docker-compose.dev.yaml exec -it app bash	

spring-init:
	- curl https://start.spring.io/starter.zip \
	-d dependencies=web \
	-d javaVersion=17 \
	-d type=maven-project \
	-o spring-project.zip

	- unzip spring-project.zip -d ./temp-spring
	- mv temp-spring/* ./
	- rm -rf temp-spring spring-project.zip	
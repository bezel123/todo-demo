default: all

all: build run

build:
	@mvn compile
	@mvn package -Dmaven.test.skip=true

run:
	@java -jar target/todo-demo-0.0.1-SNAPSHOT.jar

test:
	@mvn test

docker:
	@docker-compose rm -fv
	@docker-compose up

docker-build:
	@docker-compose rm -fv
	@docker-compose up --build

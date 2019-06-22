default: all

all: build run

build:
	@mvn compile
	@mvn package

run:
	@java -jar target/todo-demo-0.0.1-SNAPSHOT.jar

test:
	@mvn test
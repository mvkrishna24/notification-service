.PHONY: up down build test logs clean kafka-topics

## Start all infrastructure + app containers in detached mode
up:
	docker-compose up -d

## Stop all containers (keeps volumes)
down:
	docker-compose down

## Compile and package the JAR (skip tests for speed)
build:
	mvn clean package -DskipTests

## Run the full test suite
test:
	mvn test

## Tail logs for the app container
logs:
	docker-compose logs -f app

## Stop containers AND destroy named volumes (full reset)
clean:
	docker-compose down -v

## Create required Kafka topics (run after `make up` and Kafka is healthy)
kafka-topics:
	docker-compose exec kafka kafka-topics \
		--bootstrap-server localhost:9092 \
		--create --if-not-exists \
		--topic notification-events \
		--partitions 3 \
		--replication-factor 1
	docker-compose exec kafka kafka-topics \
		--bootstrap-server localhost:9092 \
		--create --if-not-exists \
		--topic notification-events.retry \
		--partitions 3 \
		--replication-factor 1
	docker-compose exec kafka kafka-topics \
		--bootstrap-server localhost:9092 \
		--create --if-not-exists \
		--topic notification-events.dlq \
		--partitions 1 \
		--replication-factor 1
	@echo "Topics created. Listing:"
	docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list

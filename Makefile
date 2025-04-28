.PHONY: help setup generate-env run-backend clean

help:
	@echo "Usage:"
	@echo "  make setup         - Install Maven wrapper if needed"
	@echo "  make generate-env  - Interactively create a .env file"
	@echo "  make run-backend   - Build and run the Spring Boot API in Docker"
	@echo "  make clean         - Remove Docker container"

setup:
	./mvnw -v || chmod +x mvnw

generate-env:
	python generate_env.py

run:
	-docker rm -f user_api_backend
	docker build -t user-api-backend .
	docker run -p 8081:8081 --name user_api_backend --rm user-api-backend

clean:
	-docker rm -f user_api_backend

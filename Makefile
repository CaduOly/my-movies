.PHONY: help build up down logs clean

help:
	@echo "Comandos disponíveis:"
	@echo "  make build   - Compila o projeto (mvn clean package)"
	@echo "  make up      - Sobe containers (docker compose up)"
	@echo "  make down    - Derruba containers"
	@echo "  make logs    - Mostra logs do app"
	@echo "  make clean   - Remove containers e volumes"

build:
	mvn clean package -DskipTests

test:
	mvn test

up: build
	@echo "Iniciando Docker Compose..."
	docker compose up -d
	@echo "Aguardando app estar pronto..."
	@sleep 5
	@echo ""
	@echo "✓ App pronto em http://localhost:8080"
	@docker compose logs app | grep -i "acesso:"

down:
	docker compose down

logs:
	docker compose logs -f app

clean:
	docker compose down -v
	rm -rf target

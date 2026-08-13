.PHONY: help build start down logs clean

help:
	@echo "Comandos disponíveis:"
	@echo "  make build   - Compila e gera o .war"
	@echo "  make start   - Sobe containers (docker compose up)"
	@echo "  make down    - Derruba containers"
	@echo "  make logs    - Mostra logs do app"
	@echo "  make clean   - Remove containers e volumes"

build:
	mvn clean package -DskipTests

test:
	mvn test

start: build
	@echo "Iniciando Docker Compose..."
	docker compose up -d --build
	@echo "Aguardando app estar pronto..."
	@sleep 5
	@echo ""
	@echo "✓ App pronto em http://localhost:8080"
	@docker compose logs app | grep -i "acesso:"

down:
	docker compose down

restart:
	@echo "Reiniciando Docker Compose..."
	make down
	make start

logs:
	docker compose logs -f app

clean:
	docker compose down -v
	rm -rf target

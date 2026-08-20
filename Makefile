.PHONY: help build start down logs clean test restart

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
	mvn clean test

start:
	@echo "Iniciando Docker Compose..."
	docker compose up -d --build
	@echo "Aguardando app estar pronto..."
	@until docker compose logs app 2>&1 | grep -q -i "Server startup\|Acesso:"; do sleep 1; done
	@echo ""
	@echo "✓ App pronto em http://localhost:8080"
	@docker compose logs app | grep -i "acesso:" || true

down:
	docker compose down

restart: down start

logs:
	docker compose logs -f app

clean:
	docker compose down -v
	rm -rf target

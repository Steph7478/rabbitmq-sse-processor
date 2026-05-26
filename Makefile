.PHONY: help dev prod-native prod-jvm stop clean logs

help:
	@echo "Available commands:"
	@echo "  make dev         - Start development mode (hot-reload + debug)"
	@echo "  make prod-native - Start production mode (Native Image)"
	@echo "  make prod-jvm    - Start production mode (JVM)"
	@echo "  make stop        - Stop all services"
	@echo "  make clean       - Stop and remove volumes"
	@echo "  make logs-dev    - Show dev logs"
	@echo "  make logs-prod   - Show production logs"

dev:
	@chmod +x docker/scripts/dev.sh
	@./docker/scripts/dev.sh

prod-native:
	@chmod +x docker/scripts/prod-native.sh
	@./docker/scripts/prod-native.sh

prod-jvm:
	@chmod +x docker/scripts/prod-jvm.sh
	@./docker/scripts/prod-jvm.sh

stop:
	@chmod +x docker/scripts/stop.sh
	@./docker/scripts/stop.sh

clean:
	@chmod +x docker/scripts/clean.sh
	@./docker/scripts/clean.sh

logs-dev:
	docker logs -f rabbitmq

logs-prod-native:
	docker logs -f rabbitmq-test-app-prod-native

logs-prod-jvm:
	docker logs -f rabbitmq-test-app-prod-jvm

shell-dev:
	docker exec -it rabbitmq sh

shell-prod-native:
	docker exec -it rabbitmq-test-app-prod-native sh

shell-prod-jvm:
	docker exec -it rabbitmq-test-app-prod-jvm sh

status:
	@echo "Dev:"
	@docker ps --filter "name=rabbitmq" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" || true
	@echo ""
	@echo "Prod Native:"
	@docker ps --filter "name=rabbitmq-test-prod-native" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" || true
	@echo ""
	@echo "Prod JVM:"
	@docker ps --filter "name=rabbitmq-test-prod-jvm" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" || true

run-containers: build-auth-service build-account-service build-job-service
	docker-compose up -d --build

stop-containers:
	docker-compose down

build-auth-service:
	cd auth-service; ./gradlew build -x test

build-account-service:
	cd account-service; ./gradlew build -x test

build-job-service:
	cd job-service; ./gradlew build -x test

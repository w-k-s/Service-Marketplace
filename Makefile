# JAVA_HOME should point to Java 15 JDK e.g. /Library/Java/JavaVirtualMachines/adopt-openjdk-15.0.1/Contents/Home
# You can use a command like export JAVA_HOME=`/usr/libexec/java_home -v <version>`
# e.g. export JAVA_HOME=`/usr/libexec/java_home -v 15.0.1`
#
# Java is provided explicitly to gradle because I find it confusing the way IntelliJ has 3 separate places to provide the Java version
# and they don't automatically synchronise with one another.

run-containers: build-auth-service build-customer-service build-order-service build-service-provider-service build-api-gateway
	docker compose up -d --build --remove-orphans

stop-containers:
	docker compose down

build-api-gateway:
	cd api-gateway; ./gradlew build -Dorg.gradle.java.home=$(JAVA_HOME) -x test

build-auth-service:
	cd auth-service; ./gradlew build -Dorg.gradle.java.home=$(JAVA_HOME) -x test

build-customer-service:
	cd customer-service; ./gradlew build -Dorg.gradle.java.home=$(JAVA_HOME) -x test

build-order-service:
	cd order-service; ./gradlew build -Dorg.gradle.java.home=$(JAVA_HOME) -x test

build-service-provider-service:
	cd service-provider-service; ./gradlew build -Dorg.gradle.java.home=$(JAVA_HOME) -x test
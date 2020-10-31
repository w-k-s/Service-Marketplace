# JAVA_HOME should point to Java 13 JDK e.g. /Library/Java/JavaVirtualMachines/jdk-13.0.1.jdk/Contents/Home
#
# Java is provided explicitly to gradle because I find it confusing the way IntelliJ has 3 separate places to provide the Java version
# and they don't automatically synchronise with one another.

run-containers: build-auth-service build-account-service build-job-service
	docker-compose up -d --build

stop-containers:
	docker-compose down

build-auth-service:
	cd auth-service; ./gradlew build -Dorg.gradle.java.home=$(JAVA_HOME) -x test

build-account-service:
	cd account-service; ./gradlew build -Dorg.gradle.java.home=$(JAVA_HOME) -x test

build-job-service:
	cd job-service; ./gradlew build -Dorg.gradle.java.home=$(JAVA_HOME) -x test
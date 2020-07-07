import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.0.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
	kotlin("plugin.jpa") version "1.3.72"
}

group = "com.wks.servicesmarketplace"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

sourceSets.getByName("main") {
	java.srcDir("src/main/java")
	java.srcDir("src/main/kotlin")
	java.srcDir("src/main/resources")
}
sourceSets.getByName("test") {
	java.srcDir("src/test/kotlin")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.hibernate.validator:hibernate-validator:6.0.16.Final")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310") // adds support for ZonedDateTime on Jacksom

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.axonframework:axon-spring-boot-starter:4.3.4"){
		exclude("org.axonframework", "axon-server-connector") // to auto configure EmbeddedEventStore (https://docs.axoniq.io/reference-guide/configuring-infrastructure-components/event-processing/event-bus-and-event-store)
	}

	implementation("com.graphql-java:graphql-spring-boot-starter:5.0.2")
	implementation("com.graphql-java:graphiql-spring-boot-starter:5.0.2")
	implementation("com.graphql-java:graphql-java-tools:5.2.4")

	runtimeOnly("org.postgresql:postgresql")

	implementation("org.springframework.boot:spring-boot-starter-amqp")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.springframework.amqp:spring-rabbit-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

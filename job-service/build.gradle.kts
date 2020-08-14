import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.0.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	id("org.liquibase.gradle") version "2.0.3"
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
	jcenter()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.hibernate.validator:hibernate-validator:6.0.16.Final")

	implementation("org.keycloak:keycloak-spring-boot-starter:11.0.0")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310") // adds support for ZonedDateTime on Jacksom

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.axonframework:axon-spring-boot-starter:4.3.4")

	implementation("com.graphql-java-kickstart:graphql-spring-boot-starter:7.1.0")
	implementation("com.graphql-java-kickstart:graphiql-spring-boot-starter:7.1.0")
	implementation("com.apollographql.federation:federation-graphql-java-support:0.5.0") // jCenter

	// JWK
	implementation("org.bitbucket.b_c:jose4j:0.7.2")

	// PostgreSQL
	runtimeOnly("org.postgresql:postgresql")

	// Liquibase
	liquibaseRuntime("org.liquibase:liquibase-core:3.8.1")
	liquibaseRuntime("javax.xml.bind:jaxb-api:2.3.1")
	liquibaseRuntime("org.postgresql:postgresql")
	liquibaseRuntime("ch.qos.logback:logback-core:1.2.3")
	liquibaseRuntime("ch.qos.logback:logback-classic:1.2.3")

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

liquibase {
	activities.register("main") {
		this.arguments = mapOf(
				"logLevel" to "info",
				"changeLogFile" to "src/main/resources/liquibase/jobService.changelog.xml",
				"url" to project.extra.properties["mainUrl"],
				"username" to project.extra.properties["username"],
				"password" to project.extra.properties["password"]
		)
	}
	runList = "main"
}

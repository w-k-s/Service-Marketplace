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
java.sourceCompatibility = JavaVersion.VERSION_15

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
	maven {
		name = "GitHubPackages"
		url = uri("https://maven.pkg.github.com/w-k-s/Service-Marketplace")
		credentials {
			username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
			password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
		}
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.hibernate.validator:hibernate-validator:6.0.16.Final")

	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.jdbi:jdbi3-core:3.20.0")

	implementation("com.wks.servicemarketplace:common:0.0.6")
	implementation("com.wks.servicemarketplace:auth-service-api:0.0.1")
	implementation("com.wks.servicemarketplace:service-provider-api:0.0.3")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310") // adds support for java.time on Jacksom

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.javamoney:moneta:1.1")

	// Country Codes
	implementation("com.neovisionaries:nv-i18n:1.27")

	// JWK
	implementation("org.bitbucket.b_c:jose4j:0.7.2")

	// PostgreSQL
	runtimeOnly("org.postgresql:postgresql")

	// Liquibase
	implementation("org.liquibase:liquibase-core:3.8.1")
	implementation("javax.xml.bind:jaxb-api:2.3.1")
	implementation("com.sun.xml.bind:jaxb-impl:2.3.1")
	implementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")
	implementation("javax.activation:activation:1.1.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}

}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "13"
	}
}

liquibase {
	activities.register("main") {
		this.arguments = mapOf(
				"logLevel" to "info",
				"changeLogFile" to "src/main/resources/liquibase/orderService.changelog.xml",
				"url" to project.extra.properties["mainUrl"],
				"username" to project.extra.properties["username"],
				"password" to project.extra.properties["password"]
		)
	}
	runList = "main"
}

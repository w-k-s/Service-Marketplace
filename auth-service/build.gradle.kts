plugins {
    kotlin("jvm") version "1.3.72"
    id("org.liquibase.gradle") version "2.0.3"
}

group = "com.wks.servicemarketplace.authservice"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/w-k-s/Service-Marketplace")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.wks.servicemarketplace.authservice.AuthServiceApplication"
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.glassfish.jersey.containers:jersey-container-jetty-http:2.31")
    implementation("org.glassfish.jersey.inject:jersey-hk2:2.31")
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    // Internal Libraries
    implementation("com.wks.servicemarketplace:common:0.0.3")
    implementation("com.wks.servicemarketplace:auth-service-api:0.0.1")
    implementation("com.wks.servicemarketplace:customer-service-api:0.0.1")

    // Config
    implementation("com.ufoscout.properlty:properlty-kotlin:1.8.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("org.slf4j:slf4j-api:1.7.30")

    // Jackson
    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.4.1") // Jackson Provider for Jax-RS
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.0") // Support for java 8's time API
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.1")
    implementation("javax.activation:activation:1.1.1") // Needed by jackson-jaxrs-json-provider

    // FusionAuth
    implementation("io.fusionauth:fusionauth-java-client:1.20.0")

    // Quartz
    implementation("org.quartz-scheduler:quartz:2.3.0")

    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql:42.2.12")

    // Hikari
    implementation("com.zaxxer:HikariCP:3.4.5")

    // JOOQ
    implementation("org.jooq:jooq:3.12.3")
    implementation("org.jooq:jooq-meta:3.12.3")

    // RabbitMQ
    implementation("com.rabbitmq:amqp-client:5.9.0")

    // Liquibase
    implementation("org.liquibase:liquibase-core:3.8.1")

    //Retrofit
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "13"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "13"
    }
}

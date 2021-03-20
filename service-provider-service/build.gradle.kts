import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.liquibase.gradle") version "2.0.3"
}
group = "com.wks.servicemarketplace"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.wks.servicemarketplace.serviceproviderservice.ServiceProviderServiceApplication"
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "13"
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")

    implementation("com.wks.servicemarketplace:common:1.0-SNAPSHOT")
    implementation("com.wks.servicemarketplace:auth-service-api:1.0-SNAPSHOT")
    implementation("com.wks.servicemarketplace:service-provider-messaging:1.0-SNAPSHOT")
    implementation("org.glassfish.jersey.containers:jersey-container-jetty-http:2.31")
    implementation("org.glassfish.jersey.inject:jersey-hk2:2.31")
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    // Quartz
    implementation("org.quartz-scheduler:quartz:2.3.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("org.slf4j:slf4j-log4j12:1.7.30")

    // Validation
    implementation("org.glassfish:javax.el:3.0.0")
    implementation("javax.el:javax.el-api:3.0.0")
    implementation("org.hibernate:hibernate-validator:6.1.5.Final")

    // Jackson
    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.4.1") // Jackson Provider for Jax-RS
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.0") // Support for java 8's time API
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.1")
    implementation("javax.activation:activation:1.1.1") // Needed by jackson-jaxrs-json-provider

    // RabbitMQ
    implementation("com.rabbitmq:amqp-client:5.9.0")

    // FusionAuth
    implementation("io.fusionauth:fusionauth-java-client:1.20.0")

    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql:42.2.12")

    // Hikari
    implementation("com.zaxxer:HikariCP:3.4.5")

    // JOOQ
    implementation("org.jooq:jooq:3.12.3")
    implementation("org.jooq:jooq-meta:3.12.3")

    // Liquibase
    liquibaseRuntime("org.liquibase:liquibase-core:3.8.1")
    liquibaseRuntime("javax.xml.bind:jaxb-api:2.3.1")
    liquibaseRuntime("org.postgresql:postgresql:42.2.12")
    liquibaseRuntime("ch.qos.logback:logback-core:1.2.3")
    liquibaseRuntime("ch.qos.logback:logback-classic:1.2.3")

    // JWT
    implementation("org.bitbucket.b_c:jose4j:0.7.2")

    // Country Codes
    implementation("com.neovisionaries:nv-i18n:1.27")

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.6.1")
}

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
                "logLevel" to "info",
                "changeLogFile" to "src/main/resources/liquibase/serviceProviderService.changelog.xml",
                "url" to project.extra.properties["mainUrl"],
                "username" to project.extra.properties["username"],
                "password" to project.extra.properties["password"]
        )
    }
    runList = "main"
}
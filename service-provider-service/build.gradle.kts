import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion : String by project
val ktorVersion : String by project
val quartzVersion: String by project
val properltyVersion: String by project
val koinVersion: String by project

plugins {
    kotlin("jvm")
}
group = "com.wks.servicemarketplace"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.wks.servicemarketplace.serviceproviderservice.Application"
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
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    implementation("com.wks.servicemarketplace:common:1.0-SNAPSHOT")
    implementation("com.wks.servicemarketplace:auth-service-api:1.0-SNAPSHOT")
    implementation("com.wks.servicemarketplace:service-provider-api:1.0-SNAPSHOT")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")

    // Quartz
    implementation("org.quartz-scheduler:quartz:$quartzVersion")

    // Config
    implementation("com.ufoscout.properlty:properlty-kotlin:$properltyVersion")

    // DI
    implementation("org.koin:koin-ktor:$koinVersion")
    implementation("org.koin:koin-logger-slf4j:$koinVersion")

    // Validation
    implementation("org.glassfish:javax.el:3.0.0")
    implementation("javax.el:javax.el-api:3.0.0")
    implementation("org.hibernate:hibernate-validator:6.1.5.Final")

    // Jackson
    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.4.1") // Jackson Provider for Jax-RS
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.0") // Support for java 8's time API
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.1")

    // RabbitMQ
    implementation("com.rabbitmq:amqp-client:5.9.0")

    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql:42.2.12")

    // Hikari
    implementation("com.zaxxer:HikariCP:3.4.5")

    // JOOQ
    implementation("org.jooq:jooq:3.12.3")
    implementation("org.jooq:jooq-meta:3.12.3")

    // Liquibase
    implementation("org.liquibase:liquibase-core:3.8.1")

    // Result Type
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.11")

    // Country Codes
    implementation("com.neovisionaries:nv-i18n:1.27")

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.6.1")

}
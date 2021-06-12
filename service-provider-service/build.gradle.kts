import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion       =   "1.4.31"
val ktorVersion         =   "1.5.3"
val koinVersion         =   "2.2.2"
val quartzVersion       =   "2.3.0"
val properltyVersion    =   "1.8.1"
val jacksonVersion      =   "2.11.1"
val amqpVersion         =   "5.9.0"
val postgresqlVersion   =   "42.2.12"
val hikariVersion       =   "3.4.5"
val jooqVersion         =   "3.12.3"
val liquibaseVersion    =   "3.8.1"
val countryCodesVersion =   "1.27"

plugins {
    kotlin("jvm") version "1.4.31"
}
group = "com.wks.servicemarketplace"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
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

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.wks.servicemarketplace.serviceproviderservice.ApplicationKt"
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

    implementation("com.wks.servicemarketplace:common:0.0.7")
    implementation("com.wks.servicemarketplace:auth-service-api:0.0.1")
    implementation("com.wks.servicemarketplace:service-provider-api:0.0.4")

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
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion") // Support for java 8's time API
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // RabbitMQ
    implementation("com.rabbitmq:amqp-client:$amqpVersion")

    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")

    // Hikari
    implementation("com.zaxxer:HikariCP:$hikariVersion")

    // JOOQ
    implementation("org.jooq:jooq:$jooqVersion")
    implementation("org.jooq:jooq-meta:$jooqVersion")

    // Liquibase
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")

    // Result Type
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.11")

    // Country Codes
    implementation("com.neovisionaries:nv-i18n:$countryCodesVersion")

    // Protocol Buffers
    implementation("com.google.protobuf:protobuf-java:3.0.0")

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.6.1")
}
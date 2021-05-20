import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion : String by project
val ktorVersion : String by project
val quartzVersion: String by project
val properltyVersion: String by project
val koinVersion: String by project
val jacksonVersion: String by project
val amqpVersion: String by project
val postgresqlVersion: String by project
val hikariVersion: String by project
val jooqVersion: String by project
val liquibaseVersion: String by project
val countryCodesVersion: String by project

plugins {
    kotlin("jvm")
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
            password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
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

    implementation("com.wks.servicemarketplace:common:0.0.3")
    implementation("com.wks.servicemarketplace:auth-service-api:0.0.1")
    implementation("com.wks.servicemarketplace:service-provider-api:0.0.1")

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

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.6.1")
}
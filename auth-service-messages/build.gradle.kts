import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}
group = "com.wks.servicemarketplace"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.wks.servicemarketplace:common:1.0-SNAPSHOT")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.11.0")

    // RabbitMQ
    implementation("com.rabbitmq:amqp-client:5.9.0")

    testImplementation(kotlin("test-junit5"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "13"
}
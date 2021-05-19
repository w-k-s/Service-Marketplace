import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    `maven-publish`
}
//group = "com.wks.servicemarketplace"
//version = "1.0-SNAPSHOT"

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
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.wks.servicemarketplace:common:0.0.3")

    // Validation
    implementation("org.glassfish:javax.el:3.0.0")
    implementation("javax.el:javax.el-api:3.0.0")
    implementation("org.hibernate:hibernate-validator:6.1.5.Final")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.11.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")

    // RabbitMQ
    implementation("com.rabbitmq:amqp-client:5.9.0")

    testImplementation(kotlin("test-junit5"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "13"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.wks.servicemarketplace"
            artifactId = "auth-service-api"
            version = "0.0.1"

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/w-k-s/Service-Marketplace")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
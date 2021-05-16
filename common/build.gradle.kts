import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    `maven-publish`
}
//group = "com.wks.servicemarketplace"
//version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Validation
    implementation("org.glassfish:javax.el:3.0.0")
    implementation("javax.el:javax.el-api:3.0.0")
    implementation("org.hibernate:hibernate-validator:6.1.5.Final")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.11.0")

    // JWT
    implementation("org.bitbucket.b_c:jose4j:0.7.2")

    // Country Codes
    implementation("com.neovisionaries:nv-i18n:1.27")

    testImplementation(kotlin("test-junit5"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "13"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.wks.servicemarketplace"
            artifactId = "common"
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


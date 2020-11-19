plugins {
    kotlin("jvm") version "1.3.72"
}

group = "com.wks.servicemarketplace.authservice"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
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

    // GraphQL
    implementation("com.graphql-java:graphql-java:14.1")
    implementation("com.graphql-java:graphql-java-tools:5.2.4")
    implementation("io.gqljf:graphql-java-federation:0.1.16") // jCenter

    // RabbitMQ
    implementation("com.rabbitmq:amqp-client:5.9.0")

    // FusionAuth
    implementation("io.fusionauth:fusionauth-java-client:1.20.0")

    // JWT
    implementation("org.bitbucket.b_c:jose4j:0.7.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "13"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "13"
    }
}
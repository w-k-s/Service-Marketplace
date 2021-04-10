rootProject.name = "service-provider-service"

pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
    }
}

includeBuild("../common")
includeBuild("../auth-service-api")
includeBuild("../service-provider-api")

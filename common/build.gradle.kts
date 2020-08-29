plugins {
    kotlin("multiplatform") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
}

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    jvm()
    js {
        browser()
    }
}

dependencies {
    "commonMainApi"(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-core", version = "1.0.0-RC")
    "commonMainApi"(group = "org.jetbrains.kotlinx", name = "kotlinx-html", version = "0.7.2")
}

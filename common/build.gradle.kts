plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
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

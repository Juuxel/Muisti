plugins {
    kotlin("js") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
}

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    js {
        browser()
    }
}

dependencies {
    implementation(project(":common"))
}

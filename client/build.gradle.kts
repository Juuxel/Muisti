plugins {
    kotlin("js")
    kotlin("plugin.serialization")
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

plugins {
    java
    kotlin("jvm") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(project(":common"))

    implementation(group = "io.javalin", name = "javalin", version = "3.10.0")
    implementation(group = "info.picocli", name = "picocli", version = "4.5.1")

    runtimeOnly(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
}

base {
    archivesBaseName = "muisti"
}

application {
    mainClassName = "juuxel.muisti.server.ServerKt"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    jar {
        archiveClassifier.set("slim")
        manifest {
            attributes("Main-Class" to application.mainClassName)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        manifest {
            attributes("Main-Class" to application.mainClassName)
        }
    }

    getByName<JavaExec>("run") {
        workingDir = project.mkdir("run")
    }

    processResources {
        val jsOutput = project(":client").tasks["browserDistribution"]
        dependsOn(jsOutput)

        inputs.property("version", version)
        filesMatching("version.properties") {
            expand("version" to version)
        }

        from(jsOutput.outputs) {
            include("client.js")
            into("static")

            filter {
                // Ignore the source mapping as we don't bundle it
                if ("sourceMappingURL" in it) {
                    "" // should be null but kt dsl doesn't allow that
                } else {
                    it
                }
            }

            rename(".+\\.js", "script.js")
        }

        from(rootProject.file("COPYING"))
    }
}

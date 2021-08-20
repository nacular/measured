buildscript {
    val kotlinVersion: String by System.getProperties()

    repositories {
        maven       { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        mavenCentral()
    }

    dependencies {
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    val kotlinVersion: String by System.getProperties()

    id ("org.jetbrains.kotlin.multiplatform") version kotlinVersion
    id ("org.jetbrains.dokka"               ) version "0.10.0"
    id("maven-publish")
    signing
}

repositories {
    maven       { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
    jcenter     ()
}

kotlin {
    val releaseBuild = project.hasProperty("release")

    jvm().compilations.all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    js {
        browser {
            testTask {
                enabled = false
            }
        }
    }.compilations.all {
        kotlinOptions {
            moduleKind = "umd"
            sourceMap  = !releaseBuild
            if (sourceMap) {
                sourceMapEmbedSources = "always"
            }
        }
    }

    val junitVersion: String by project

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation("junit:junit:$junitVersion")
                implementation(kotlin("test-junit"))
            }
        }

        js().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    classifier = "javadoc"
    from(tasks.dokka)
}

publishing {
    publications.withType<MavenPublication>().apply {
        val jvm by getting {
            artifact(dokkaJar)
        }
        all {
            pom {
                name.set       ("Measured"                           )
                description.set("Units of measure for Kotlin"        )
                url.set        ("https://github.com/nacular/measured")
                licenses {
                    license {
                        name.set("MIT"                                )
                        url.set ("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set  ("pusolito"     )
                        name.set("Nicholas Eddy")
                    }
                }
                scm {
                    url.set                ("https://github.com/nacular/measured.git"      )
                    connection.set         ("scm:git:git://github.com/nacular/measured.git")
                    developerConnection.set("scm:git:git://github.com/nacular/measured.git")
                }
            }
        }
    }

    repositories {
        maven {
            val releaseBuild = project.hasProperty("release")

            url = uri(when {
                releaseBuild -> "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                else         -> "https://oss.sonatype.org/content/repositories/snapshots"
            })

            credentials {
                username = findProperty("suser")?.toString()
                password = findProperty("spwd" )?.toString()
            }
        }
    }
}

signing {
    setRequired({
        project.hasProperty("release") && gradle.taskGraph.hasTask("publish")
    })
    useGpgCmd()
    sign(publishing.publications)
}

tasks {
    val dokka by getting(org.jetbrains.dokka.gradle.DokkaTask::class) {
        outputDirectory = "$buildDir/javadoc"
        outputFormat    = "html"

        multiplatform {
            val js  by creating {}
            val jvm by creating {}
        }
    }
}
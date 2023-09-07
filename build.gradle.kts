import org.gradle.configurationcache.extensions.capitalized
import java.net.URL

buildscript {
    val kotlinVersion: String by System.getProperties()

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    val kotlinVersion: String by System.getProperties()

    id ("org.jetbrains.kotlin.multiplatform") version kotlinVersion
    id ("org.jetbrains.dokka"               ) version "1.9.0"
    id ("maven-publish"                     )
    signing
}

repositories {
    mavenCentral()
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
                implementation("org.junit.jupiter:junit-jupiter:$junitVersion")
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
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

tasks.dokkaHtml {
    moduleName.set(project.name.capitalized())
    outputDirectory.set(buildDir.resolve("javadoc"))

    dokkaSourceSets.configureEach {
        includeNonPublic.set(false)

        // Do not output deprecated members. Applies globally, can be overridden by packageOptions
        skipDeprecated.set(true)

        // Emit warnings about not documented members. Applies globally, also can be overridden by packageOptions
        reportUndocumented.set(true)

        // Do not create index pages for empty packages
        skipEmptyPackages.set(true)

        includes.from("Module.md")

        sourceLink {
            localDirectory.set(rootProject.projectDir)
            remoteUrl.set(URL("https://github.com/nacular/measured/tree/master"))
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLink {
            url.set(URL("https://kotlinlang.org/api/latest/jvm/stdlib/"))
        }
    }
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

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().download    = false
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.0.0"
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().disableGranularWorkspaces()
}
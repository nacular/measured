import org.gradle.configurationcache.extensions.capitalized
import java.net.URL

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlinVersion.get()}")
    }
}

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    id ("maven-publish"     )
    signing
}

repositories {
    mavenCentral()
}

kotlin {
    val releaseBuild = project.hasProperty("release")

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    js  {
        browser {
            testTask {
                enabled = false
            }
        }

        compilations.all {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap  = !releaseBuild
                if (sourceMap) {
                    sourceMapEmbedSources = "always"
                }
            }
        }
    }

    @Suppress("OPT_IN_USAGE")
    wasmJs {
        browser {
            testTask { enabled = false }
        }
        compilations.all {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap  = !releaseBuild
                if (sourceMap) {
                    sourceMapEmbedSources = "always"
                }
            }
        }
    }

    listOf(
        iosX64               (),
        iosArm64             (),
        iosSimulatorArm64    (),
        watchosX64           (),
        watchosArm64         (),
        watchosSimulatorArm64(),
        macosX64             (),
        macosArm64           (),
        tvosX64              (),
        tvosArm64            (),
        tvosSimulatorArm64   (),
    ).forEach {
        it.binaries.framework {
            baseName = "measured"
            isStatic = true
        }

        val isMacOS   = System.getProperty("os.name"   ) == "Mac OS X"
        val osVersion = System.getProperty("os.version").toDoubleOrNull() ?: 0.0

        // Use this flag if using MacOS 14 or newer
        if (isMacOS && osVersion >= 14.0) {
            it.compilations.all {
                compilerOptions.configure {
                    freeCompilerArgs.add("-linker-options")
                    freeCompilerArgs.add("-ld64"          )
                }
            }
        }
    }

    linuxX64  ()
    linuxArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(kotlin("stdlib-common"))
        }

        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        jvmTest.dependencies {
            implementation(libs.bundles.test.libs)
            implementation(kotlin("test-junit"))
        }

        jsTest.dependencies {
            implementation(kotlin("test-js"))
        }

        val wasmJsTest by getting {
            dependencies {
                implementation(kotlin("test-wasm-js"))
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

    outputDirectory.set(layout.buildDirectory.dir("javadoc"))

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
//    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().download    = false
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.0.0"
}

//rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
//    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().disableGranularWorkspaces()
//}
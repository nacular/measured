import org.jetbrains.kotlin.gradle.dsl.JsModuleKind.MODULE_UMD
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode.SOURCE_MAP_SOURCE_CONTENT_ALWAYS
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlinVersion.get()}")
    }
}

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokkaHtml)
    alias(libs.plugins.kover)
    id ("maven-publish"     )
    signing
    alias(libs.plugins.nmcp            ) apply false
    alias(libs.plugins.nmcp.aggregation)
}

repositories {
    mavenCentral()
}

kotlin {
    val releaseBuild = project.hasProperty("release")
    val libName      = project.name.lowercase(Locale.getDefault())

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    js  {
        browser {
            testTask {
                enabled = false
            }
        }

        compilerOptions {
            moduleKind.set(MODULE_UMD)
            sourceMap.set(!releaseBuild)
            if (sourceMap.get()) {
                sourceMapEmbedSources.set(SOURCE_MAP_SOURCE_CONTENT_ALWAYS)
            }
        }
    }

    @Suppress("OPT_IN_USAGE")
    wasmJs {
        browser {
            testTask { enabled = false }
        }
        compilerOptions {
            moduleKind.set(MODULE_UMD)
            sourceMap.set(!releaseBuild)
            if (sourceMap.get()) {
                sourceMapEmbedSources.set(SOURCE_MAP_SOURCE_CONTENT_ALWAYS)
            }
        }
    }

    listOf(
        iosX64               (),
        iosArm64             (),
        iosSimulatorArm64    (),
        watchosArm64         (),
        watchosSimulatorArm64(),
        macosArm64           (),
        tvosArm64            (),
        tvosSimulatorArm64   (),
    ).forEach {
        it.binaries.framework {
            baseName = libName
            isStatic = true
            binaryOption("bundleVersion", project.version.toString())
        }

        val isMacOS   = System.getProperty("os.name"   ) == "Mac OS X"
        val osVersion = System.getProperty("os.version").toDoubleOrNull() ?: 0.0

        // Use this flag if using MacOS 14 or newer
        if (isMacOS && osVersion >= 14.0) {
//            it.binaries.sharedLib {
//                baseName = libName
//            }
            it.compilations.all {
                compileTaskProvider.configure{
                    compilerOptions {
                        freeCompilerArgs.add("-linker-options")
                        freeCompilerArgs.add("-ld64"          )
                    }
                }
            }
        }
    }

    androidNativeX64  ()
    androidNativeArm32()

    listOf(
        mingwX64  (), // Windows x64
        linuxX64  (),
        linuxArm64(),
    ).forEach {
        it.binaries.sharedLib {
            baseName = libName
        }
    }

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

        wasmJsTest.dependencies {
            implementation(kotlin("test-wasm-js"))
        }
    }
}

// To generate documentation in HTML
val dokkaHtmlJar by tasks.registering(Jar::class) {
    description = "A HTML Documentation JAR containing Dokka HTML"
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

dokka {
    dokkaPublications.html {
        moduleName.set(project.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
        moduleVersion.set(project.version.toString())
        outputDirectory.set(layout.buildDirectory.dir("documentation/html"))
        dokkaSourceSets.configureEach {
            // Do not output deprecated members. Applies globally, can be overridden by packageOptions
            skipDeprecated.set(true)

            // Emit warnings about not documented members. Applies globally, also can be overridden by packageOptions
            reportUndocumented.set(true)

            // Do not create index pages for empty packages
            skipEmptyPackages.set(true)

            includes.from("Module.md")

            sourceLink {
                localDirectory.set(rootProject.projectDir)
                remoteUrl("https://github.com/nacular/measured/tree/master")
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLinks {
                // "kotlin-stdlib" is a unique name for this link configuration
                create("kotlin-stdlib") {
                    url("https://kotlinlang.org/api/latest/jvm/stdlib/")
                    // Optional: Dokka usually finds this automatically,
                    // but you can set it explicitly if needed.
                    // packageListUrl("https://kotlinlang.org")
                }
            }
        }
    }
}

publishing {
    publications.withType<MavenPublication>().apply {
        val jvm by getting {
            artifact(dokkaHtmlJar)
        }
        all {
            pom {
                name.set       ("Measured"                             )
                description.set("Intuitive, type-safe units for Kotlin")
                url.set        ("https://github.com/nacular/measured"  )
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
}

signing {
    setRequired({
        project.hasProperty("release") && gradle.taskGraph.hasTask("publish")
    })
    useGpgCmd()
    sign(publishing.publications)
}

nmcpAggregation {
    centralPortal {
        username       = findProperty("mavenCentralUsername")?.toString()
        password       = findProperty("mavenCentralPassword")?.toString()
        publishingType = "USER_MANAGED"
    }

    // Publish all projects that apply the 'maven-publish' plugin
    publishAllProjectsProbablyBreakingProjectIsolation()
}
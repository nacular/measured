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
}

version  = "0.1.0"
group    = "io.nacular.measured"

repositories {
    maven       { url = uri("http://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
    jcenter     ()
}

kotlin {
    jvm().compilations.all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    js {
        browser.testTask {
            enabled = false
        }
    }.compilations.all {
        kotlinOptions {
            sourceMap             = true
            moduleKind            = "umd"
            sourceMapEmbedSources = "always"
        }
    }

    val mockkVersion   : String by project
    val junitVersion   : String by project
    val mockkJsVersion : String by project

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
                implementation("io.mockk:mockk-common:$mockkVersion")
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation("junit:junit:$junitVersion")
                implementation(kotlin("test-junit"))

                implementation("io.mockk:mockk:$mockkVersion")
            }
        }

        js().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }

        js().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("io.mockk:mockk-js:$mockkJsVersion")
            }
        }
    }
}
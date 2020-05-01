buildscript {
    repositories {
        jcenter()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.fabric.io/public") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://maven-central.storage.googleapis.com") }
        maven { url = uri("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-dev") }
        maven { url = uri("https://dl.bintray.com/icerockdev/moko") }
        maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
        maven { url = uri("https://dl.bintray.com/badoo/maven") }

    }
    dependencies {
        val kotlinVersion = "1.3.50"
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.2.1.RELEASE")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("gradle.plugin.de.fuerstenau:BuildConfigPlugin:1.1.8")
        classpath("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
        classpath("com.android.tools.build:gradle:3.5.0")
        classpath("org.jetbrains.kotlin:kotlin-android-extensions:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.fabric.io/public") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://maven-central.storage.googleapis.com") }
        maven { url = uri("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-dev") }
        maven { url = uri("https://dl.bintray.com/icerockdev/moko") }
        maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
        maven { url = uri("https://dl.bintray.com/badoo/maven") }

    }
    configurations.create("compileClasspath")
}

tasks.register("clean").configure {
    delete("build")
}
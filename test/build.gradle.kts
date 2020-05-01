import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin")
    id("application")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
    id("kotlin-jpa")
    id("de.fuerstenau.buildconfig")
    id("idea")
    id("kotlinx-serialization")
}

group = "test"
version = "1.0-SNAPSHOT"

val junitVersion = "4.12"
val kotlinVersion = "1.3.50"
val mockitoVersion = "2.21.0"
val mockitoKotlin = "1.6.0"
val mapVersion = "0.9.0"
val serializationVersion = "0.2.9"
val firebaseAdminVersion = "6.2.0"
val reaktive = "1.1.0"

dependencies {

    //kotlin
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    compile("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion")

    //tests
    compile("junit:junit:$junitVersion")
    compile("org.mockito:mockito-core:$mockitoVersion")
    compile("com.nhaarman:mockito-kotlin:$mockitoKotlin")

    //spring
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-aop")
    compile("org.springframework.boot:spring-boot-starter-cache")
    compile("org.springframework.boot:spring-boot-starter-websocket")
    compile("org.springframework.boot:spring-boot-starter-jdbc")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.springframework.boot:spring-boot-starter-security")
    compile("org.springframework.boot:spring-boot-starter-validation")
    compile("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    //swagger
    compile("io.springfox:springfox-swagger2:2.9.2")
    compile("io.springfox:springfox-swagger-ui:2.9.2")

    //google
    compile("com.google.cloud:google-cloud-translate:1.79.0")
    compile("com.google.cloud:google-cloud-logging:1.70.0")
    compile("com.google.cloud:google-cloud-bigquery:1.101.0")
    compile("com.google.maps:google-maps-services:$mapVersion")
    compile("com.google.firebase:firebase-admin:$firebaseAdminVersion")

//    database
    compile("org.hibernate:hibernate-java8")
    compile("org.postgresql:postgresql")
    compile("org.flywaydb:flyway-core")

//    retrofit
    compile("com.squareup.retrofit2:retrofit:2.4.0")
    compile("com.squareup.retrofit2:converter-jackson:2.4.0")
    compile("com.squareup.okhttp3:logging-interceptor:3.10.0")
    compile("com.squareup.okhttp3:okhttp:3.10.0")

    //utils
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.7")
    compile("org.apache.commons:commons-lang3:3.0")
    compile("io.jsonwebtoken:jjwt:0.9.0")
    compile("org.apache.poi:poi-ooxml:3.15")
    compile("org.jsoup:jsoup:1.11.3")
    compile("org.telegram:telegrambots:4.4.0.2")
    implementation("com.aparapi:aparapi:2.0.0")
//    implementation("com.badoo.reaktive:reaktive:1.1.0-rc3")
}


configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
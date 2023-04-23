import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22" apply false
    id("org.springframework.boot") version "3.0.6" apply false
    id("io.spring.dependency-management") version "1.1.0"
    id("jacoco")
    id("org.sonarqube") version "4.0.0.2929"
}

java.sourceCompatibility = JavaVersion.VERSION_17

allprojects {
    group = "pl.piter"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.sonarqube")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

jacoco {
    toolVersion = "0.8.7"
}

sonarqube {
    properties {
        property("sonar.projectKey", "PiotrMichalowski96_nba-mvp-ai-selector")
        property("sonar.organization", "piotrmichalowski96")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

tasks.sonar {
    dependsOn(tasks.jacocoTestReport)
}
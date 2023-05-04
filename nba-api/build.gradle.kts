extra["springCloudVersion"] = "2022.0.2"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.kafka:spring-kafka")
    implementation(project(":commons-lib"))
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("it.ozimov:embedded-redis:0.7.3") {
        exclude("commons-logging", "commons-logging")
        exclude("org.slf4j", "slf4j-simple")
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

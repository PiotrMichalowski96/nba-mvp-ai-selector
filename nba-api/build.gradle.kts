extra["springCloudVersion"] = "2022.0.2"
extra["kafkaVersion"] = "3.3.2"

dependencies {
    implementation(project(":commons-lib"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.apache.kafka:kafka-streams:${property("kafkaVersion")}")
    implementation("org.apache.kafka:kafka-clients:${property("kafkaVersion")}")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.awaitility:awaitility-kotlin:4.2.0")
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

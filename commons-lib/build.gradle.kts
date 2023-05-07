tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
}
plugins {
    id("java")
}

group = "edu.io"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.jetbrains:annotations:24.0.1") // Dodana zależność
}

tasks.test {
    useJUnitPlatform()
}
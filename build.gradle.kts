plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "edu.io.net"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/game_common-1.2.10.jar"))
    implementation(files("libs/game_connector_lib-1.2.10.jar"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("edu.io.net.Main")
}
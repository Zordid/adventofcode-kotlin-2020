plugins {
    application
    kotlin("jvm") version "1.4.20"
}

application {
    mainClassName = "AdventOfCodeKt"
}

group = "de.zordid"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("org.reflections", "reflections", "0.9.12")
    implementation("guru.nidi:graphviz-kotlin:0.17.1")
    //implementation("ch.qos.logback", "logback-classic", "1.2.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

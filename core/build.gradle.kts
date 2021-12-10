plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

object Versions {
    const val kotest = "5.0.0"
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
    testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
    testImplementation("io.kotest:kotest-property:${Versions.kotest}")
    testImplementation("io.kotest:kotest-framework-datatest:${Versions.kotest}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
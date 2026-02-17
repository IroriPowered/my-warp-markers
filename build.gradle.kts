plugins {
    id("java")
}

group = "cc.irori"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://maven.hytale.com/release")
    maven("https://maven.hytale.com/pre-release")
}

dependencies {
    compileOnly(libs.hytale)
}

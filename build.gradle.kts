plugins {
    id("java")
}

group = "cc.irori"
version = "1.0.0"

dependencies {
    compileOnly(files("libs/HytaleServer.jar"))
}

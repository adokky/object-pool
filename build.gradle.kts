plugins {
    id("io.github.adokky.quick-mpp") version "0.15"
    id("io.github.adokky.quick-publish") version "0.15"
}

group = "io.github.adokky"
version = "1.0"

dependencies {
    commonMainImplementation("io.github.adokky:karamel-utils-core:0.1.1")
}

mavenPublishing {
    pom {
        description = "Fast and minimalistic thread-local object pool"
        inceptionYear = "2025"
    }
}
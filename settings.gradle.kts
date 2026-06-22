include(
    "jabel-javac-plugin",
    "example"
)

rootProject.name = "jabel"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.develocity") version "4.4.3"
}

val isCiServer = System.getenv().containsKey("CI")

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree.set("yes")
        publishing.onlyIf { _ -> false }
        if (isCiServer) {
            tag("CI")
        }
    }
}

buildCache {
    local {
        // Disable on CI b/c local cache will always be empty and will be cleared after run
        isEnabled = !isCiServer
    }
}
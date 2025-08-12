pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // needed for KSP plugin
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Temple Vehicle Tracker"
include(":app")

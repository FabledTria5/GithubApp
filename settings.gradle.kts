pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GithubApp"
include(":app")
include(":domain")
include(":data")
include(":navigation")
include(":features")
include(":features:home")
include(":features:repository")
include(":core")

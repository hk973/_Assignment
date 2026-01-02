pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "android_movie_app"
include(":app")

// Include KMP shared library
includeBuild("../kmp_shared_movie") {
    dependencySubstitution {
        substitute(module("io.github.hariomkankatti.kmplibrary:library")).using(project(":library"))
    }
}
 
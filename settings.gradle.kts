@Suppress("UnstableApiUsage")
dependencyResolutionManagement {

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }
}

include(":app")
rootProject.name = "Sarawan"

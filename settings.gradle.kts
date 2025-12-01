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
        // ===> CHUYỂN DÒNG ĐÓ VÀO ĐÂY MỚI ĐÚNG <===
        // Đây là nơi khai báo kho chứa thư viện cho dự án
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "SE07203-B5"
include(":app")
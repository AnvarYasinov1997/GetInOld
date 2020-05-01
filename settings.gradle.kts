rootProject.name = "Demo"

include(":test")

project(":test").projectDir = File(settingsDir, "/test")

enableFeaturePreview("GRADLE_METADATA")


// Top-level build file. Plugin versions are declared here and applied per-module.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// The project lives under OneDrive, which syncs build output mid-write and intermittently locks
// files Gradle is trying to delete/replace (e.g. "Failed to clean up output files for task
// :app:packageDebug"). Redirecting every module's build/ directory outside the synced tree
// removes OneDrive from the loop entirely. Source files stay in place and are still synced/backed
// up normally — only generated, disposable build output moves.
val externalBuildRoot = file("C:/AndroidBuilds/rankridge_app")
layout.buildDirectory.set(externalBuildRoot.resolve("root"))
subprojects {
    layout.buildDirectory.set(externalBuildRoot.resolve(name))
}

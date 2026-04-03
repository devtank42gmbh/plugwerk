// Thin aggregator — all build logic lives in each example project.
// Run `./gradlew build` here to build all examples at once.

val lifecycleTasks = listOf("build", "clean", "assemble", "check")

lifecycleTasks.forEach { taskName ->
    tasks.register(taskName) {
        group = "build"
        dependsOn(
            gradle.includedBuilds.map { it.task(":$taskName") },
        )
    }
}

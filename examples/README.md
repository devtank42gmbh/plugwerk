# Plugwerk Examples

Example projects demonstrating how to integrate the Plugwerk client SDK.

## Structure

Each example is an **independent Gradle project** that can be built and tested
on its own. The top-level `examples/` directory is a composite-build aggregator.

| Directory | Description |
|---|---|
| [`plugwerk-java-cli-example`](plugwerk-java-cli-example/) | Java CLI application using picocli + PF4J with dynamic plugin commands |

## Building

### All examples at once

```bash
cd examples/
./gradlew build
```

### A single example independently

```bash
cd examples/plugwerk-java-cli-example/
./gradlew build
```

## Prerequisites

Examples use Gradle composite builds to resolve `plugwerk-spi` directly from
the main project source. No `publishToMavenLocal` is needed as long as you
build from within the plugwerk repository checkout.

> **Standalone mode**: If you build an example outside of the monorepo, run
> `./gradlew publishToMavenLocal` in the main project first. The examples fall
> back to Maven Local when the composite build is not available.

## Adding a New Example

1. Create a new directory under `examples/` (e.g., `plugwerk-spring-example/`)
2. Add `settings.gradle.kts` with `includeBuild("../..")` and submodule includes
3. Add `build.gradle.kts` with project-specific conventions and lifecycle task delegation
4. Add `gradle/libs.versions.toml` with required dependencies
5. Symlink `gradlew`, `gradlew.bat`, and `gradle/wrapper/*` from the parent
6. Register in `examples/settings.gradle.kts` via `includeBuild("plugwerk-spring-example")`

See `plugwerk-java-cli-example/` as a reference.

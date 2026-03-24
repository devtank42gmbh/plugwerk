plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.openapi.generator)
}

kotlin {
    jvmToolchain(21)
}

tasks.compileKotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

tasks.compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

dependencies {
    implementation(platform(libs.jackson.bom))
    implementation(libs.jackson.annotations)
    implementation(libs.jakarta.validation.api)
}

val specFile = "${rootProject.projectDir}/plugwerk-api/src/main/resources/openapi/plugwerk-api.yaml"

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set(specFile)
    outputDir.set("${layout.buildDirectory.get()}/generated")
    apiPackage.set("io.plugwerk.api")
    modelPackage.set("io.plugwerk.api.model")
    configOptions.set(
        mapOf(
            "interfaceOnly" to "true",
            "useTags" to "true",
            "useSpringBoot3" to "true",
            "documentationProvider" to "none",
            "serializationLibrary" to "jackson",
            "enumPropertyNaming" to "UPPERCASE",
            "reactive" to "false",
        ),
    )
    globalProperties.set(
        mapOf(
            "models" to "",
        ),
    )
}

// Exclude generated ApiUtil.kt (Spring dependency) — only model classes needed
tasks.openApiGenerate {
    doLast {
        delete("${layout.buildDirectory.get()}/generated/src/main/kotlin/io/plugwerk/api/ApiUtil.kt")
    }
}

sourceSets {
    main {
        kotlin {
            srcDir("${layout.buildDirectory.get()}/generated/src/main/kotlin")
        }
    }
}

tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
}

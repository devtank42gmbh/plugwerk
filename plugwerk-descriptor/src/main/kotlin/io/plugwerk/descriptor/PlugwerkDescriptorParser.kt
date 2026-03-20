package io.plugwerk.descriptor

import io.plugwerk.common.version.isValidSemVer
import tools.jackson.core.JacksonException
import tools.jackson.core.StreamReadConstraints
import tools.jackson.dataformat.yaml.YAMLFactory
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.kotlinModule
import java.io.InputStream
import java.util.jar.JarInputStream

class PlugwerkDescriptorParser {

    private val yamlMapper: YAMLMapper = run {
        val factory = YAMLFactory.builder()
            .streamReadConstraints(
                StreamReadConstraints.builder()
                    .maxDocumentLength(512_000L)
                    .maxStringLength(65_536)
                    .maxNestingDepth(20)
                    .maxNumberLength(20)
                    .build(),
            )
            .build() as YAMLFactory
        YAMLMapper.builder(factory)
            .addModule(kotlinModule())
            .build()
    }

    fun parse(inputStream: InputStream): PlugwerkDescriptor {
        val root = try {
            yamlMapper.readValue(inputStream, PlugwerkYamlRoot::class.java)
        } catch (e: JacksonException) {
            throw DescriptorParseException("Failed to parse plugwerk.yml: ${e.message}", e)
        }
        return toDescriptor(root.plugwerk)
    }

    fun parseFromJar(jarStream: InputStream): PlugwerkDescriptor {
        JarInputStream(jarStream).use { jar ->
            var entry = jar.nextJarEntry
            while (entry != null) {
                validateEntryName(entry.name)
                if (entry.name == "plugwerk.yml") {
                    return parse(jar)
                }
                entry = jar.nextJarEntry
            }
        }
        throw DescriptorNotFoundException("No plugwerk.yml found in JAR")
    }

    private fun toDescriptor(yaml: PlugwerkYamlDescriptor): PlugwerkDescriptor {
        val id = yaml.id
            ?: throw DescriptorParseException("Required field 'id' is missing in plugwerk.yml")
        val version = yaml.version
            ?: throw DescriptorParseException("Required field 'version' is missing in plugwerk.yml")
        val name = yaml.name
            ?: throw DescriptorParseException("Required field 'name' is missing in plugwerk.yml")

        if (!version.isValidSemVer()) {
            throw DescriptorParseException("Invalid SemVer version '$version' in plugwerk.yml")
        }

        val dependencies = yaml.requires?.plugins?.map { dep ->
            PluginDependency(
                id = dep.id ?: throw DescriptorParseException("Plugin dependency missing 'id'"),
                version = dep.version ?: "*",
            )
        } ?: emptyList()

        return PlugwerkDescriptor(
            id = id,
            version = version,
            name = name,
            description = yaml.description,
            author = yaml.author,
            license = yaml.license,
            namespace = yaml.namespace,
            categories = yaml.categories,
            tags = yaml.tags,
            requiresSystemVersion = yaml.requires?.systemVersion,
            requiresApiLevel = yaml.requires?.apiLevel,
            pluginDependencies = dependencies,
            icon = yaml.icon,
            screenshots = yaml.screenshots,
            homepage = yaml.homepage,
            repository = yaml.repository,
        )
    }
}

internal fun validateEntryName(name: String) {
    require(!name.contains("..") && !name.contains('\u0000')) {
        "Suspicious JAR entry name: $name"
    }
}

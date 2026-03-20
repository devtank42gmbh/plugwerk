package io.plugwerk.descriptor

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class DescriptorResolver(
    private val plugwerkParser: PlugwerkDescriptorParser = PlugwerkDescriptorParser(),
    private val manifestParser: Pf4jManifestParser = Pf4jManifestParser(),
) {

    fun resolve(jarStream: InputStream): PlugwerkDescriptor {
        val bytes = jarStream.readAllBytes()

        return tryParse { plugwerkParser.parseFromJar(ByteArrayInputStream(bytes)) }
            ?: tryParse { manifestParser.parseFromJar(ByteArrayInputStream(bytes)) }
            ?: throw DescriptorNotFoundException(
                "No descriptor found in JAR (tried plugwerk.yml, MANIFEST.MF, plugin.properties)",
            )
    }

    private fun tryParse(block: () -> PlugwerkDescriptor): PlugwerkDescriptor? = try {
        block()
    } catch (_: DescriptorNotFoundException) {
        null
    }
}

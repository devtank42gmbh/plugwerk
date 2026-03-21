package io.plugwerk.server.service.storage

import io.plugwerk.server.service.ArtifactStorageException
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists

@Service
@ConditionalOnProperty(prefix = "plugwerk.storage", name = ["type"], havingValue = "fs", matchIfMissing = true)
class FilesystemArtifactStorageService(properties: StorageProperties) : ArtifactStorageService {

    private val root: Path = Path.of(properties.fs.root)

    override fun store(key: String, content: InputStream, contentLength: Long): String {
        val target = resolveKey(key)
        try {
            Files.createDirectories(target.parent)
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING)
        } catch (e: Exception) {
            throw ArtifactStorageException("Failed to store artifact at key '$key'", e)
        }
        return key
    }

    override fun retrieve(key: String): InputStream {
        val path = resolveKey(key)
        if (!path.exists()) throw ArtifactStorageException("Artifact not found for key '$key'")
        return try {
            Files.newInputStream(path)
        } catch (e: Exception) {
            throw ArtifactStorageException("Failed to retrieve artifact at key '$key'", e)
        }
    }

    override fun delete(key: String) {
        val path = resolveKey(key)
        try {
            Files.deleteIfExists(path)
        } catch (e: Exception) {
            throw ArtifactStorageException("Failed to delete artifact at key '$key'", e)
        }
    }

    override fun exists(key: String): Boolean = resolveKey(key).exists()

    private fun resolveKey(key: String): Path = root.resolve(key).normalize().also {
        require(it.startsWith(root)) { "Key '$key' resolves outside storage root" }
    }
}

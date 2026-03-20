package io.plugwerk.common.extension

import io.plugwerk.common.model.InstallResult
import org.pf4j.ExtensionPoint
import java.nio.file.Path

/**
 * Extension point for plugin download, verification, and installation.
 */
interface PlugwerkInstaller : ExtensionPoint {
    fun download(namespace: String, pluginId: String, version: String, targetDir: Path): Path
    fun install(namespace: String, pluginId: String, version: String): InstallResult
    fun uninstall(pluginId: String): InstallResult
    fun verifyChecksum(artifactPath: Path, expectedSha256: String): Boolean
}

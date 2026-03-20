package io.plugwerk.common.extension

import io.plugwerk.common.model.UpdateInfo
import org.pf4j.ExtensionPoint

/**
 * Extension point for checking available plugin updates.
 */
interface PlugwerkUpdateChecker : ExtensionPoint {
    fun checkForUpdates(namespace: String, installedPlugins: Map<String, String>): List<UpdateInfo>
    fun getAvailableUpdates(namespace: String): List<UpdateInfo>
}

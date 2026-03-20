package io.plugwerk.common.extension

import io.plugwerk.common.model.PluginInfo
import io.plugwerk.common.model.PluginReleaseInfo
import io.plugwerk.common.model.SearchCriteria
import org.pf4j.ExtensionPoint

/**
 * Extension point for plugin catalog queries.
 */
interface PlugwerkCatalog : ExtensionPoint {
    fun listPlugins(namespace: String): List<PluginInfo>
    fun getPlugin(namespace: String, pluginId: String): PluginInfo?
    fun searchPlugins(namespace: String, criteria: SearchCriteria): List<PluginInfo>
    fun getPluginReleases(namespace: String, pluginId: String): List<PluginReleaseInfo>
    fun getPluginRelease(namespace: String, pluginId: String, version: String): PluginReleaseInfo?
}

package io.plugwerk.common.model

data class PluginInfo(
    val pluginId: String,
    val name: String,
    val description: String? = null,
    val author: String? = null,
    val license: String? = null,
    val namespace: String? = null,
    val categories: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val latestVersion: String? = null,
    val status: PluginStatus,
    val icon: String? = null,
    val homepage: String? = null,
    val repository: String? = null,
)

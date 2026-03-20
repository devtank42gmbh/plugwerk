package io.plugwerk.descriptor

data class PlugwerkDescriptor(
    val id: String,
    val version: String,
    val name: String,
    val description: String? = null,
    val author: String? = null,
    val license: String? = null,
    val namespace: String? = null,
    val categories: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val requiresSystemVersion: String? = null,
    val requiresApiLevel: Int? = null,
    val pluginDependencies: List<PluginDependency> = emptyList(),
    val icon: String? = null,
    val screenshots: List<String> = emptyList(),
    val homepage: String? = null,
    val repository: String? = null,
)

data class PluginDependency(val id: String, val version: String)

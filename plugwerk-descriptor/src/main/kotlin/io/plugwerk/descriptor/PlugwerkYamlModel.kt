package io.plugwerk.descriptor

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlugwerkYamlRoot(val plugwerk: PlugwerkYamlDescriptor)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlugwerkYamlDescriptor(
    val id: String? = null,
    val version: String? = null,
    val name: String? = null,
    val description: String? = null,
    val author: String? = null,
    val license: String? = null,
    val requires: PlugwerkYamlRequires? = null,
    val namespace: String? = null,
    val categories: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val icon: String? = null,
    val screenshots: List<String> = emptyList(),
    val homepage: String? = null,
    val repository: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlugwerkYamlRequires(
    @JsonProperty("system-version")
    val systemVersion: String? = null,
    @JsonProperty("api-level")
    val apiLevel: Int? = null,
    val plugins: List<PlugwerkYamlPluginDependency> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlugwerkYamlPluginDependency(val id: String? = null, val version: String? = null)

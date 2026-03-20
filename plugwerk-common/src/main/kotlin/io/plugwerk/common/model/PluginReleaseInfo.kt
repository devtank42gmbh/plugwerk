package io.plugwerk.common.model

data class PluginReleaseInfo(
    val pluginId: String,
    val version: String,
    val artifactSha256: String,
    val requiresSystemVersion: String? = null,
    val status: ReleaseStatus,
    val downloadUrl: String,
)

package io.plugwerk.common.model

data class UpdateInfo(
    val pluginId: String,
    val currentVersion: String,
    val availableVersion: String,
    val release: PluginReleaseInfo,
)

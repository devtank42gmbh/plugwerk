package io.plugwerk.common.model

sealed class InstallResult {
    data class Success(val pluginId: String, val version: String) : InstallResult()

    data class Failure(val pluginId: String, val version: String, val reason: String) : InstallResult()
}

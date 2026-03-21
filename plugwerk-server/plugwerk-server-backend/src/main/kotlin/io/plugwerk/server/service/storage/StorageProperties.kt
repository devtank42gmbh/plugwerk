package io.plugwerk.server.service.storage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "plugwerk.storage")
data class StorageProperties(val type: String = "fs", val fs: FsProperties = FsProperties()) {
    data class FsProperties(val root: String = "/var/plugwerk/artifacts")
}

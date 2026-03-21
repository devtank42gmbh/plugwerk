package io.plugwerk.server.repository

import io.plugwerk.common.model.ReleaseStatus
import io.plugwerk.server.domain.PluginEntity
import io.plugwerk.server.domain.PluginReleaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface PluginReleaseRepository : JpaRepository<PluginReleaseEntity, UUID> {

    fun findByPluginAndVersion(plugin: PluginEntity, version: String): Optional<PluginReleaseEntity>

    fun findAllByPluginOrderByCreatedAtDesc(plugin: PluginEntity): List<PluginReleaseEntity>

    fun findAllByPluginAndStatus(plugin: PluginEntity, status: ReleaseStatus): List<PluginReleaseEntity>

    fun existsByPluginAndVersion(plugin: PluginEntity, version: String): Boolean
}

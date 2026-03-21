package io.plugwerk.server.repository

import io.plugwerk.common.model.PluginStatus
import io.plugwerk.server.domain.NamespaceEntity
import io.plugwerk.server.domain.PluginEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface PluginRepository : JpaRepository<PluginEntity, UUID> {

    fun findByNamespaceAndPluginId(namespace: NamespaceEntity, pluginId: String): Optional<PluginEntity>

    fun findAllByNamespace(namespace: NamespaceEntity): List<PluginEntity>

    fun findAllByNamespaceAndStatus(namespace: NamespaceEntity, status: PluginStatus): List<PluginEntity>

    fun existsByNamespaceAndPluginId(namespace: NamespaceEntity, pluginId: String): Boolean
}

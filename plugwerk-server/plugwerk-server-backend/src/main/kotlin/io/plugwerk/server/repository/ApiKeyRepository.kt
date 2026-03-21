package io.plugwerk.server.repository

import io.plugwerk.server.domain.ApiKeyEntity
import io.plugwerk.server.domain.NamespaceEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ApiKeyRepository : JpaRepository<ApiKeyEntity, UUID> {

    fun findByKeyHash(keyHash: String): Optional<ApiKeyEntity>

    fun findAllByNamespaceAndRevokedFalse(namespace: NamespaceEntity): List<ApiKeyEntity>
}

package io.plugwerk.server.repository

import io.plugwerk.server.domain.NamespaceEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface NamespaceRepository : JpaRepository<NamespaceEntity, UUID> {

    fun findBySlug(slug: String): Optional<NamespaceEntity>

    fun existsBySlug(slug: String): Boolean
}

package io.plugwerk.server.service

import io.plugwerk.server.domain.NamespaceEntity
import io.plugwerk.server.repository.NamespaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NamespaceService(private val namespaceRepository: NamespaceRepository) {

    fun findBySlug(slug: String): NamespaceEntity = namespaceRepository.findBySlug(slug)
        .orElseThrow { NamespaceNotFoundException(slug) }

    fun findAll(): List<NamespaceEntity> = namespaceRepository.findAll()

    @Transactional
    fun create(slug: String, ownerOrg: String, settings: String? = null): NamespaceEntity {
        if (namespaceRepository.existsBySlug(slug)) throw NamespaceAlreadyExistsException(slug)
        return namespaceRepository.save(NamespaceEntity(slug = slug, ownerOrg = ownerOrg, settings = settings))
    }

    @Transactional
    fun update(slug: String, ownerOrg: String? = null, settings: String? = null): NamespaceEntity {
        val entity = findBySlug(slug)
        ownerOrg?.let { entity.ownerOrg = it }
        settings?.let { entity.settings = it }
        return namespaceRepository.save(entity)
    }

    @Transactional
    fun delete(slug: String) {
        val entity = findBySlug(slug)
        namespaceRepository.delete(entity)
    }
}

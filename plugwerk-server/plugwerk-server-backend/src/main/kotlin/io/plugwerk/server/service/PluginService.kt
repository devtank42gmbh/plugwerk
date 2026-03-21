package io.plugwerk.server.service

import io.plugwerk.common.model.PluginStatus
import io.plugwerk.server.domain.PluginEntity
import io.plugwerk.server.repository.PluginRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PluginService(private val pluginRepository: PluginRepository, private val namespaceService: NamespaceService) {

    fun findByNamespaceAndPluginId(namespaceSlug: String, pluginId: String): PluginEntity {
        val namespace = namespaceService.findBySlug(namespaceSlug)
        return pluginRepository.findByNamespaceAndPluginId(namespace, pluginId)
            .orElseThrow { PluginNotFoundException(namespaceSlug, pluginId) }
    }

    fun findAllByNamespace(namespaceSlug: String, status: PluginStatus? = null): List<PluginEntity> {
        val namespace = namespaceService.findBySlug(namespaceSlug)
        return if (status != null) {
            pluginRepository.findAllByNamespaceAndStatus(namespace, status)
        } else {
            pluginRepository.findAllByNamespace(namespace)
        }
    }

    @Transactional
    fun create(
        namespaceSlug: String,
        pluginId: String,
        name: String,
        description: String? = null,
        author: String? = null,
        license: String? = null,
        homepage: String? = null,
        repository: String? = null,
        icon: String? = null,
        categories: Array<String> = emptyArray(),
        tags: Array<String> = emptyArray(),
    ): PluginEntity {
        val namespace = namespaceService.findBySlug(namespaceSlug)
        if (pluginRepository.existsByNamespaceAndPluginId(namespace, pluginId)) {
            throw PluginAlreadyExistsException(namespaceSlug, pluginId)
        }
        return pluginRepository.save(
            PluginEntity(
                namespace = namespace,
                pluginId = pluginId,
                name = name,
                description = description,
                author = author,
                license = license,
                homepage = homepage,
                repository = repository,
                icon = icon,
                categories = categories,
                tags = tags,
            ),
        )
    }

    @Transactional
    fun update(
        namespaceSlug: String,
        pluginId: String,
        name: String? = null,
        description: String? = null,
        author: String? = null,
        license: String? = null,
        homepage: String? = null,
        repository: String? = null,
        icon: String? = null,
        categories: Array<String>? = null,
        tags: Array<String>? = null,
        status: PluginStatus? = null,
    ): PluginEntity {
        val entity = findByNamespaceAndPluginId(namespaceSlug, pluginId)
        name?.let { entity.name = it }
        description?.let { entity.description = it }
        author?.let { entity.author = it }
        license?.let { entity.license = it }
        homepage?.let { entity.homepage = it }
        repository?.let { entity.repository = it }
        icon?.let { entity.icon = it }
        categories?.let { entity.categories = it }
        tags?.let { entity.tags = it }
        status?.let { entity.status = it }
        return pluginRepository.save(entity)
    }

    @Transactional
    fun delete(namespaceSlug: String, pluginId: String) {
        val entity = findByNamespaceAndPluginId(namespaceSlug, pluginId)
        pluginRepository.delete(entity)
    }
}

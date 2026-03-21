package io.plugwerk.server.service

import io.plugwerk.api.model.Pf4jPluginInfo
import io.plugwerk.api.model.Pf4jPluginsJson
import io.plugwerk.api.model.Pf4jReleaseInfo
import io.plugwerk.common.model.PluginStatus
import io.plugwerk.common.model.ReleaseStatus
import io.plugwerk.server.repository.NamespaceRepository
import io.plugwerk.server.repository.PluginReleaseRepository
import io.plugwerk.server.repository.PluginRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Service
@Transactional(readOnly = true)
class Pf4jCompatibilityService(
    private val namespaceRepository: NamespaceRepository,
    private val pluginRepository: PluginRepository,
    private val releaseRepository: PluginReleaseRepository,
    @Value("\${plugwerk.server.base-url}") private val baseUrl: String,
) {

    fun buildPluginsJson(namespaceSlug: String): Pf4jPluginsJson {
        val namespace = namespaceRepository.findBySlug(namespaceSlug)
            .orElseThrow { NamespaceNotFoundException(namespaceSlug) }

        val pf4jPlugins = pluginRepository.findAllByNamespaceAndStatus(namespace, PluginStatus.ACTIVE)
            .map { plugin ->
                val releases = releaseRepository.findAllByPluginAndStatus(plugin, ReleaseStatus.PUBLISHED)
                    .map { release ->
                        Pf4jReleaseInfo(
                            version = release.version,
                            url = URI(
                                "$baseUrl/api/v1/namespaces/$namespaceSlug/plugins/${plugin.pluginId}/releases/${release.version}/download",
                            ),
                            date = release.createdAt.toLocalDate(),
                            requires = release.requiresSystemVersion,
                        )
                    }
                Pf4jPluginInfo(
                    id = plugin.pluginId,
                    description = plugin.description,
                    provider = plugin.author,
                    projectUrl = plugin.homepage,
                    releases = releases,
                )
            }

        return Pf4jPluginsJson(plugins = pf4jPlugins)
    }
}

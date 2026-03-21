package io.plugwerk.server.service

import io.plugwerk.common.model.PluginStatus
import io.plugwerk.common.model.ReleaseStatus
import io.plugwerk.server.domain.NamespaceEntity
import io.plugwerk.server.domain.PluginEntity
import io.plugwerk.server.domain.PluginReleaseEntity
import io.plugwerk.server.repository.NamespaceRepository
import io.plugwerk.server.repository.PluginReleaseRepository
import io.plugwerk.server.repository.PluginRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.util.Optional
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class Pf4jCompatibilityServiceTest {

    @Mock lateinit var namespaceRepository: NamespaceRepository

    @Mock lateinit var pluginRepository: PluginRepository

    @Mock lateinit var releaseRepository: PluginReleaseRepository

    lateinit var pf4jCompatibilityService: Pf4jCompatibilityService

    private val namespace = NamespaceEntity(slug = "acme", ownerOrg = "ACME Corp")
    private val plugin =
        PluginEntity(namespace = namespace, pluginId = "my-plugin", name = "My Plugin", description = "A plugin")

    @BeforeEach
    fun setUp() {
        pf4jCompatibilityService = Pf4jCompatibilityService(
            namespaceRepository,
            pluginRepository,
            releaseRepository,
            baseUrl = "http://localhost:8080",
        )
    }

    @Test
    fun `buildPluginsJson returns pf4j compatible structure`() {
        val release = PluginReleaseEntity(
            plugin = plugin,
            version = "1.0.0",
            artifactSha256 = "sha256abc",
            artifactKey = "acme/my-plugin/1.0.0",
            status = ReleaseStatus.PUBLISHED,
        )
        whenever(namespaceRepository.findBySlug("acme")).thenReturn(Optional.of(namespace))
        whenever(
            pluginRepository.findAllByNamespaceAndStatus(namespace, PluginStatus.ACTIVE),
        ).thenReturn(listOf(plugin))
        whenever(
            releaseRepository.findAllByPluginAndStatus(plugin, ReleaseStatus.PUBLISHED),
        ).thenReturn(listOf(release))

        val result = pf4jCompatibilityService.buildPluginsJson("acme")

        assertThat(result.plugins).hasSize(1)
        val pf4jPlugin = result.plugins.first()
        assertThat(pf4jPlugin.id).isEqualTo("my-plugin")
        assertThat(pf4jPlugin.description).isEqualTo("A plugin")
        assertThat(pf4jPlugin.releases).hasSize(1)
        assertThat(pf4jPlugin.releases.first().version).isEqualTo("1.0.0")
        assertThat(pf4jPlugin.releases.first().url.toString())
            .isEqualTo("http://localhost:8080/api/v1/namespaces/acme/plugins/my-plugin/releases/1.0.0/download")
    }

    @Test
    fun `buildPluginsJson returns empty list when no active plugins`() {
        whenever(namespaceRepository.findBySlug("acme")).thenReturn(Optional.of(namespace))
        whenever(pluginRepository.findAllByNamespaceAndStatus(namespace, PluginStatus.ACTIVE)).thenReturn(emptyList())

        val result = pf4jCompatibilityService.buildPluginsJson("acme")

        assertThat(result.plugins).isEmpty()
    }

    @Test
    fun `buildPluginsJson throws NamespaceNotFoundException for unknown namespace`() {
        whenever(namespaceRepository.findBySlug("missing")).thenReturn(Optional.empty())

        assertFailsWith<NamespaceNotFoundException> {
            pf4jCompatibilityService.buildPluginsJson("missing")
        }
    }

    @Test
    fun `buildPluginsJson includes requiresSystemVersion when set`() {
        val release = PluginReleaseEntity(
            plugin = plugin,
            version = "2.0.0",
            artifactSha256 = "sha",
            artifactKey = "acme/my-plugin/2.0.0",
            status = ReleaseStatus.PUBLISHED,
            requiresSystemVersion = ">=3.0.0",
        )
        whenever(namespaceRepository.findBySlug("acme")).thenReturn(Optional.of(namespace))
        whenever(
            pluginRepository.findAllByNamespaceAndStatus(namespace, PluginStatus.ACTIVE),
        ).thenReturn(listOf(plugin))
        whenever(
            releaseRepository.findAllByPluginAndStatus(plugin, ReleaseStatus.PUBLISHED),
        ).thenReturn(listOf(release))

        val result = pf4jCompatibilityService.buildPluginsJson("acme")

        assertThat(result.plugins.first().releases.first().requires).isEqualTo(">=3.0.0")
    }
}

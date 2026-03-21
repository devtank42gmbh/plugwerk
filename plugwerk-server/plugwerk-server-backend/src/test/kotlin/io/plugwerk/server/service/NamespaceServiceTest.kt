package io.plugwerk.server.service

import io.plugwerk.server.domain.NamespaceEntity
import io.plugwerk.server.repository.NamespaceRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class NamespaceServiceTest {

    @Mock
    lateinit var namespaceRepository: NamespaceRepository

    @InjectMocks
    lateinit var namespaceService: NamespaceService

    @Test
    fun `findBySlug returns namespace when it exists`() {
        val entity = NamespaceEntity(slug = "acme", ownerOrg = "ACME Corp")
        whenever(namespaceRepository.findBySlug("acme")).thenReturn(Optional.of(entity))

        val result = namespaceService.findBySlug("acme")

        assertThat(result.slug).isEqualTo("acme")
        assertThat(result.ownerOrg).isEqualTo("ACME Corp")
    }

    @Test
    fun `findBySlug throws NamespaceNotFoundException when not found`() {
        whenever(namespaceRepository.findBySlug("missing")).thenReturn(Optional.empty())

        assertFailsWith<NamespaceNotFoundException> {
            namespaceService.findBySlug("missing")
        }
    }

    @Test
    fun `create saves and returns new namespace`() {
        whenever(namespaceRepository.existsBySlug("new-ns")).thenReturn(false)
        val saved = NamespaceEntity(slug = "new-ns", ownerOrg = "Org")
        whenever(namespaceRepository.save(any<NamespaceEntity>())).thenReturn(saved)

        val result = namespaceService.create("new-ns", "Org")

        assertThat(result.slug).isEqualTo("new-ns")
        verify(namespaceRepository).save(any<NamespaceEntity>())
    }

    @Test
    fun `create throws NamespaceAlreadyExistsException when slug is taken`() {
        whenever(namespaceRepository.existsBySlug("existing")).thenReturn(true)

        assertFailsWith<NamespaceAlreadyExistsException> {
            namespaceService.create("existing", "Org")
        }

        verify(namespaceRepository, never()).save(any<NamespaceEntity>())
    }

    @Test
    fun `update changes ownerOrg and settings`() {
        val entity = NamespaceEntity(slug = "acme", ownerOrg = "Old Org")
        whenever(namespaceRepository.findBySlug("acme")).thenReturn(Optional.of(entity))
        whenever(namespaceRepository.save(any<NamespaceEntity>())).thenReturn(entity)

        namespaceService.update("acme", ownerOrg = "New Org", settings = """{"key":"val"}""")

        assertThat(entity.ownerOrg).isEqualTo("New Org")
        assertThat(entity.settings).isEqualTo("""{"key":"val"}""")
    }

    @Test
    fun `delete removes namespace`() {
        val entity = NamespaceEntity(slug = "to-delete", ownerOrg = "Org")
        whenever(namespaceRepository.findBySlug("to-delete")).thenReturn(Optional.of(entity))

        namespaceService.delete("to-delete")

        verify(namespaceRepository).delete(entity)
    }

    @Test
    fun `delete throws NamespaceNotFoundException when namespace missing`() {
        whenever(namespaceRepository.findBySlug("missing")).thenReturn(Optional.empty())

        assertFailsWith<NamespaceNotFoundException> {
            namespaceService.delete("missing")
        }
    }
}

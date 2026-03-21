package io.plugwerk.server.repository

import io.plugwerk.server.AbstractRepositoryTest
import io.plugwerk.server.domain.NamespaceEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import kotlin.test.assertFailsWith

open class NamespaceRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    lateinit var namespaceRepository: NamespaceRepository

    @Test
    fun `findBySlug returns namespace when slug exists`() {
        val namespace =
            namespaceRepository.save(
                NamespaceEntity(slug = "acme", ownerOrg = "ACME Corp"),
            )

        val found = namespaceRepository.findBySlug("acme")

        assertThat(found).isPresent
        assertThat(found.get().id).isEqualTo(namespace.id!!)
        assertThat(found.get().ownerOrg).isEqualTo("ACME Corp")
    }

    @Test
    fun `findBySlug returns empty when slug does not exist`() {
        val found = namespaceRepository.findBySlug("does-not-exist")

        assertThat(found).isEmpty
    }

    @Test
    fun `existsBySlug returns true when slug exists`() {
        namespaceRepository.save(NamespaceEntity(slug = "exists-ns", ownerOrg = "Org"))

        assertThat(namespaceRepository.existsBySlug("exists-ns")).isTrue()
        assertThat(namespaceRepository.existsBySlug("missing-ns")).isFalse()
    }

    @Test
    fun `save persists settings as JSON`() {
        val namespace =
            namespaceRepository.save(
                NamespaceEntity(
                    slug = "ns-with-settings",
                    ownerOrg = "Org",
                    settings = """{"maxPlugins": 100}""",
                ),
            )

        val found = namespaceRepository.findById(namespace.id!!).orElseThrow()

        assertThat(found.settings).contains("maxPlugins")
    }

    @Test
    fun `save fails on duplicate slug`() {
        namespaceRepository.save(NamespaceEntity(slug = "duplicate", ownerOrg = "Org A"))
        namespaceRepository.flush()

        assertFailsWith<DataIntegrityViolationException> {
            namespaceRepository.saveAndFlush(NamespaceEntity(slug = "duplicate", ownerOrg = "Org B"))
        }
    }

    @Test
    fun `delete removes namespace`() {
        val namespace = namespaceRepository.save(NamespaceEntity(slug = "to-delete", ownerOrg = "Org"))

        namespaceRepository.delete(namespace)

        assertThat(namespaceRepository.findById(namespace.id!!)).isEmpty
    }
}

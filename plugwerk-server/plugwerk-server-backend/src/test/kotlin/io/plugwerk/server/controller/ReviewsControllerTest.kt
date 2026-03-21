/*
 * Plugwerk — Plugin Marketplace for the PF4J Ecosystem
 * Copyright (C) 2026 devtank42 GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package io.plugwerk.server.controller

import io.plugwerk.common.model.ReleaseStatus
import io.plugwerk.server.controller.mapper.PluginReleaseMapper
import io.plugwerk.server.domain.NamespaceEntity
import io.plugwerk.server.domain.PluginEntity
import io.plugwerk.server.domain.PluginReleaseEntity
import io.plugwerk.server.service.PluginReleaseService
import io.plugwerk.server.service.ReleaseNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ReviewsControllerTest {

    @Mock lateinit var releaseService: PluginReleaseService

    @Mock lateinit var releaseMapper: PluginReleaseMapper

    private lateinit var mockMvc: MockMvc

    private val namespace = NamespaceEntity(slug = "acme", ownerOrg = "ACME Corp")
    private val plugin = PluginEntity(
        id = UUID.randomUUID(),
        namespace = namespace,
        pluginId = "my-plugin",
        name = "My Plugin",
    )
    private val releaseId = UUID.randomUUID()
    private val draftRelease = PluginReleaseEntity(
        id = releaseId,
        plugin = plugin,
        version = "1.0.0",
        artifactSha256 = "abc123",
        artifactKey = "acme/my-plugin/1.0.0",
        status = ReleaseStatus.DRAFT,
    )

    @BeforeEach
    fun setup() {
        val controller = ReviewsController(releaseService, releaseMapper)
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `GET pending reviews returns 200 with list`() {
        whenever(releaseService.findPendingByNamespace("acme")).thenReturn(listOf(draftRelease))

        mockMvc.get("/api/v1/namespaces/acme/reviews/pending")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].pluginId") { value("my-plugin") }
                jsonPath("$[0].version") { value("1.0.0") }
            }
    }

    @Test
    fun `GET pending reviews returns empty list when none`() {
        whenever(releaseService.findPendingByNamespace("acme")).thenReturn(emptyList())

        mockMvc.get("/api/v1/namespaces/acme/reviews/pending")
            .andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
                jsonPath("$.length()") { value(0) }
            }
    }

    @Test
    fun `POST approve returns 200 with published release`() {
        val published = draftRelease.apply { status = ReleaseStatus.PUBLISHED }
        whenever(releaseService.updateStatusById(eq(releaseId), eq(ReleaseStatus.PUBLISHED))).thenReturn(published)
        whenever(releaseMapper.toDto(any(), eq("my-plugin")))
            .thenReturn(buildReleaseDto(io.plugwerk.api.model.PluginReleaseDto.Status.PUBLISHED))

        mockMvc.post("/api/v1/namespaces/acme/reviews/$releaseId/approve") {
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andExpect {
            status { isOk() }
            jsonPath("$.status") { value("published") }
        }
    }

    @Test
    fun `POST approve returns 404 when release not found`() {
        val unknownId = UUID.randomUUID()
        whenever(releaseService.updateStatusById(eq(unknownId), any()))
            .thenThrow(ReleaseNotFoundException("id=$unknownId", ""))

        mockMvc.post("/api/v1/namespaces/acme/reviews/$unknownId/approve") {
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `POST reject returns 200 with yanked release`() {
        val yanked = draftRelease.apply { status = ReleaseStatus.YANKED }
        whenever(releaseService.updateStatusById(eq(releaseId), eq(ReleaseStatus.YANKED))).thenReturn(yanked)
        whenever(releaseMapper.toDto(any(), eq("my-plugin")))
            .thenReturn(buildReleaseDto(io.plugwerk.api.model.PluginReleaseDto.Status.YANKED))

        mockMvc.post("/api/v1/namespaces/acme/reviews/$releaseId/reject") {
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andExpect {
            status { isOk() }
            jsonPath("$.status") { value("yanked") }
        }
    }

    private fun buildReleaseDto(status: io.plugwerk.api.model.PluginReleaseDto.Status) =
        io.plugwerk.api.model.PluginReleaseDto(
            id = releaseId,
            pluginId = "my-plugin",
            version = "1.0.0",
            status = status,
        )
}

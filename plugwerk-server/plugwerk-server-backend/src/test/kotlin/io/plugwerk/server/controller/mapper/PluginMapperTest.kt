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
package io.plugwerk.server.controller.mapper

import io.plugwerk.api.model.PluginDto
import io.plugwerk.server.domain.NamespaceEntity
import io.plugwerk.server.domain.PluginEntity
import io.plugwerk.spi.model.PluginStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class PluginMapperTest {

    private val mapper = PluginMapper()

    private val namespace = NamespaceEntity(slug = "acme", ownerOrg = "ACME Corp")
    private val plugin = PluginEntity(
        id = UUID.randomUUID(),
        namespace = namespace,
        pluginId = "my-plugin",
        name = "My Plugin",
        description = "A great plugin",
        author = "ACME",
        categories = arrayOf("tools", "productivity"),
        tags = arrayOf("kotlin", "pf4j"),
        status = PluginStatus.ACTIVE,
    )

    @Test
    fun `toDto maps all fields correctly`() {
        val dto = mapper.toDto(plugin, "acme", "2.0.0")

        assertThat(dto.id).isEqualTo(plugin.id)
        assertThat(dto.pluginId).isEqualTo("my-plugin")
        assertThat(dto.name).isEqualTo("My Plugin")
        assertThat(dto.description).isEqualTo("A great plugin")
        assertThat(dto.author).isEqualTo("ACME")
        assertThat(dto.namespace).isEqualTo("acme")
        assertThat(dto.status).isEqualTo(PluginDto.Status.ACTIVE)
        assertThat(dto.categories).containsExactly("tools", "productivity")
        assertThat(dto.tags).containsExactly("kotlin", "pf4j")
        assertThat(dto.latestVersion).isEqualTo("2.0.0")
    }

    @Test
    fun `toDto maps SUSPENDED status`() {
        val suspended = PluginEntity(
            id = UUID.randomUUID(),
            namespace = namespace,
            pluginId = "p",
            name = "P",
            status = PluginStatus.SUSPENDED,
        )
        assertThat(mapper.toDto(suspended, "acme").status).isEqualTo(PluginDto.Status.SUSPENDED)
    }

    @Test
    fun `toDto maps ARCHIVED status`() {
        val archived = PluginEntity(
            id = UUID.randomUUID(),
            namespace = namespace,
            pluginId = "p",
            name = "P",
            status = PluginStatus.ARCHIVED,
        )
        assertThat(mapper.toDto(archived, "acme").status).isEqualTo(PluginDto.Status.ARCHIVED)
    }

    @Test
    fun `toDto sets latestVersion to null when not provided`() {
        val dto = mapper.toDto(plugin, "acme")
        assertThat(dto.latestVersion).isNull()
    }

    @Test
    fun `toDto returns null for empty categories and tags`() {
        val minimal = PluginEntity(
            id = UUID.randomUUID(),
            namespace = namespace,
            pluginId = "p",
            name = "Minimal",
        )
        val dto = mapper.toDto(minimal, "acme")
        assertThat(dto.categories).isNull()
        assertThat(dto.tags).isNull()
    }
}

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
package io.plugwerk.server.domain

import io.plugwerk.common.model.PluginStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.UuidGenerator
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Repräsentiert einen Plugin-Eintrag im Katalog eines Namespace.
 *
 * Ein Plugin ist ein registriertes PF4J-Plugin innerhalb eines [NamespaceEntity]. Es enthält
 * alle Metadaten, die für die Auffindbarkeit im Katalog relevant sind (Name, Beschreibung,
 * Kategorien, Tags usw.), hat aber selbst keinen ausführbaren Code – dieser liegt in den
 * zugehörigen [PluginReleaseEntity]-Einträgen als versionierte Artefakte.
 *
 * **Datenmodell:** Jeder Plugin-Eintrag entspricht einer Zeile in der Tabelle `plugin`.
 * Die Kombination aus `namespace_id` und `plugin_id` ist eindeutig (Unique Constraint).
 * Ein Plugin gehört zu genau einem [NamespaceEntity] und kann beliebig viele
 * [PluginReleaseEntity]-Einträge besitzen.
 *
 * **Verwendung:**
 * - Wird von [io.plugwerk.server.repository.PluginRepository] gelesen und geschrieben.
 * - Wird von [io.plugwerk.server.service.PluginService] für CRUD-Operationen und
 *   Kataloganfragen (mit optionalem Status-Filter) verwaltet.
 * - Wird von [io.plugwerk.server.service.PluginReleaseService] beim Upload automatisch
 *   angelegt, wenn noch kein Eintrag für den Plugin-Identifier existiert.
 * - Die [io.plugwerk.server.service.Pf4jCompatibilityService] bildet aktive Plugins
 *   auf das pf4j-update-Format (`plugins.json`) ab.
 *
 * @property id Primärschlüssel, UUIDv7 (zeitbasiert, von Hibernate generiert).
 * @property namespace Der übergeordnete [NamespaceEntity] (Lazy-geladen).
 * @property pluginId Fachlicher Bezeichner des Plugins innerhalb des Namespace
 *   (entspricht der PF4J Plugin-ID, z. B. `my-awesome-plugin`).
 * @property name Anzeigename des Plugins.
 * @property description Ausführliche Beschreibung (optional, als Freitext).
 * @property author Name des Autors oder der Autorengruppe (optional).
 * @property license SPDX-Lizenz-Bezeichner des Plugins (optional, z. B. `Apache-2.0`).
 * @property homepage URL zur Projekt-Homepage (optional).
 * @property repository URL zum Quellcode-Repository (optional).
 * @property icon URL zu einem Icon-Bild für die Kataloganzeige (optional).
 * @property categories Freitextliste von Kategorien zur Filterung im Katalog.
 * @property tags Freitextliste von Schlagwörtern zur Suche im Katalog.
 * @property status Lebenszyklusstatus des Plugins ([io.plugwerk.common.model.PluginStatus]):
 *   `ACTIVE` (veröffentlicht), `SUSPENDED` (gesperrt) oder `ARCHIVED` (archiviert).
 * @property createdAt Erstellungszeitpunkt (wird automatisch gesetzt, unveränderlich).
 * @property updatedAt Zeitpunkt der letzten Änderung (wird automatisch aktualisiert).
 */
@Entity
@Table(
    name = "plugin",
    uniqueConstraints = [UniqueConstraint(columnNames = ["namespace_id", "plugin_id"])],
)
class PluginEntity(
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", updatable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "namespace_id", nullable = false, updatable = false)
    var namespace: NamespaceEntity,

    @Column(name = "plugin_id", nullable = false, length = 255)
    var pluginId: String,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "description", columnDefinition = "text")
    var description: String? = null,

    @Column(name = "author", length = 255)
    var author: String? = null,

    @Column(name = "license", length = 100)
    var license: String? = null,

    @Column(name = "homepage", length = 2048)
    var homepage: String? = null,

    @Column(name = "repository", length = 2048)
    var repository: String? = null,

    @Column(name = "icon", length = 2048)
    var icon: String? = null,

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "categories", nullable = false)
    var categories: Array<String> = emptyArray(),

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tags", nullable = false)
    var tags: Array<String> = emptyArray(),

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    var status: PluginStatus = PluginStatus.ACTIVE,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)

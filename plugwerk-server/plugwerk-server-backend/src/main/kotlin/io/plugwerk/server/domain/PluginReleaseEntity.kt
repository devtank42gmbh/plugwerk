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

import io.plugwerk.common.model.ReleaseStatus
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
 * Repräsentiert eine versionierte Release eines Plugins.
 *
 * Eine Plugin-Release ist eine konkrete, veröffentlichbare Version eines [PluginEntity].
 * Sie bindet eine SemVer-Versionsnummer an ein gespeichertes Artefakt (JAR oder ZIP)
 * und enthält alle Metadaten, die für Installation, Kompatibilitätsprüfung und
 * Abhängigkeitsauflösung benötigt werden.
 *
 * **Datenmodell:** Jede Release entspricht einer Zeile in der Tabelle `plugin_release`.
 * Die Kombination aus `plugin_id` und `version` ist eindeutig (Unique Constraint).
 * Eine Release gehört zu genau einem [PluginEntity].
 *
 * **Lebenszyklus:**
 * Neue Releases starten im Status `DRAFT` und durchlaufen folgende Zustände
 * ([io.plugwerk.common.model.ReleaseStatus]):
 * ```
 * DRAFT → PUBLISHED → DEPRECATED
 *                   → YANKED
 * ```
 * Nur `PUBLISHED`-Releases werden im Katalog angezeigt und für Update-Checks berücksichtigt.
 *
 * **Verwendung:**
 * - Wird von [io.plugwerk.server.repository.PluginReleaseRepository] gelesen und geschrieben.
 * - Wird von [io.plugwerk.server.service.PluginReleaseService] beim Artefakt-Upload angelegt:
 *   SHA-256-Prüfsumme und Artifact-Key werden beim Upload berechnet und gesetzt.
 * - [io.plugwerk.server.service.UpdateCheckService] vergleicht installierte Versionen
 *   gegen die neueste `PUBLISHED`-Release per SemVer-Vergleich.
 * - [io.plugwerk.server.service.Pf4jCompatibilityService] bildet `PUBLISHED`-Releases
 *   auf das pf4j-update-Format (`plugins.json`) ab, inklusive Download-URL.
 *
 * @property id Primärschlüssel, UUIDv7 (zeitbasiert, von Hibernate generiert).
 * @property plugin Das zugehörige [PluginEntity] (Lazy-geladen).
 * @property version SemVer-Versionsnummer dieser Release (z. B. `1.2.3`).
 * @property artifactSha256 SHA-256-Prüfsumme des gespeicherten Artefakts (Hex-kodiert,
 *   64 Zeichen). Wird beim Upload berechnet und dient zur Integritätsprüfung beim Download.
 * @property artifactKey Storage-agnostischer Schlüssel für das Artefakt im konfigurierten
 *   Storage-Backend (z. B. `acme/my-plugin/1.2.3` für Filesystem oder S3).
 *   Der Schlüssel wird von [io.plugwerk.server.service.storage.ArtifactStorageService]
 *   interpretiert und ist unabhängig vom konkreten Storage-Typ.
 * @property requiresSystemVersion Optionale SemVer-Bereichsangabe für die Mindest-/
 *   Maximalversion des Host-Systems (z. B. `>=2.0.0 & <4.0.0`).
 * @property pluginDependencies Optionale Liste von Plugin-zu-Plugin-Abhängigkeiten
 *   als JSON-Array (z. B. `[{"id":"other-plugin","version":">=1.0.0"}]`).
 * @property status Aktueller Lebenszyklusstatus der Release
 *   (Standard: [io.plugwerk.common.model.ReleaseStatus.DRAFT]).
 * @property createdAt Erstellungszeitpunkt (wird automatisch gesetzt, unveränderlich).
 * @property updatedAt Zeitpunkt der letzten Änderung (wird automatisch aktualisiert).
 */
@Entity
@Table(
    name = "plugin_release",
    uniqueConstraints = [UniqueConstraint(columnNames = ["plugin_id", "version"])],
)
class PluginReleaseEntity(
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", updatable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plugin_id", nullable = false, updatable = false)
    var plugin: PluginEntity,

    @Column(name = "version", nullable = false, length = 100)
    var version: String,

    @Column(name = "artifact_sha256", nullable = false, length = 64)
    var artifactSha256: String,

    @Column(name = "artifact_key", nullable = false, length = 1024)
    var artifactKey: String,

    @Column(name = "requires_system_version", length = 255)
    var requiresSystemVersion: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "plugin_dependencies")
    var pluginDependencies: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    var status: ReleaseStatus = ReleaseStatus.DRAFT,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)

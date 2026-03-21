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

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UuidGenerator
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Repräsentiert einen API-Schlüssel zur Authentifizierung am Plugwerk-Server.
 *
 * API-Schlüssel sind die primäre Authentifizierungsform in Phase 1 (MVP). Sie gewähren
 * Zugriff auf die API eines bestimmten [NamespaceEntity] und werden für Operationen
 * wie das Hochladen neuer Plugin-Releases verwendet.
 *
 * **Sicherheitshinweis:** Der API-Schlüssel wird **niemals im Klartext gespeichert**.
 * Nur sein SHA-256-Hash ([keyHash]) wird persistiert. Der ursprüngliche Schlüssel
 * wird ausschließlich bei der Erstellung einmalig an den Aufrufer zurückgegeben.
 *
 * **Datenmodell:** Jeder API-Schlüssel entspricht einer Zeile in der Tabelle `api_key`.
 * Der [keyHash] ist eindeutig (Unique Constraint). Ein API-Schlüssel gehört zu genau
 * einem [NamespaceEntity].
 *
 * **Verwendung:**
 * - Wird von [io.plugwerk.server.repository.ApiKeyRepository] gelesen und geschrieben.
 * - Eingehende Anfragen werden authentifiziert, indem der übermittelte Schlüssel gehasht
 *   und gegen [keyHash] verglichen wird.
 * - Schlüssel können über [revoked] deaktiviert werden, ohne sie zu löschen
 *   (Audit-Trail bleibt erhalten).
 * - Optionales [expiresAt] ermöglicht zeitlich begrenzte Schlüssel.
 *
 * @property id Primärschlüssel, UUIDv7 (zeitbasiert, von Hibernate generiert).
 * @property namespace Der [NamespaceEntity], auf den dieser Schlüssel Zugriff gewährt
 *   (Lazy-geladen).
 * @property keyHash SHA-256-Hash des API-Schlüssels (Hex-kodiert, 64 Zeichen).
 *   Wird zur Authentifizierung gegen eingehende Anfragen geprüft.
 * @property description Optionaler Freitext zur Beschreibung des Verwendungszwecks
 *   (z. B. `CI/CD Pipeline`).
 * @property revoked `true`, wenn der Schlüssel manuell widerrufen wurde und keine
 *   Authentifizierung mehr erlaubt.
 * @property expiresAt Optionaler Ablaufzeitpunkt. Nach diesem Zeitpunkt wird der
 *   Schlüssel als ungültig behandelt, auch wenn [revoked] `false` ist.
 * @property createdAt Erstellungszeitpunkt (wird automatisch gesetzt, unveränderlich).
 */
@Entity
@Table(name = "api_key")
class ApiKeyEntity(
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", updatable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "namespace_id", nullable = false, updatable = false)
    var namespace: NamespaceEntity,

    @Column(name = "key_hash", nullable = false, unique = true, length = 64)
    var keyHash: String,

    @Column(name = "description", length = 255)
    var description: String? = null,

    @Column(name = "revoked", nullable = false)
    var revoked: Boolean = false,

    @Column(name = "expires_at")
    var expiresAt: OffsetDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
)

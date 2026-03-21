package io.plugwerk.server.domain

import io.plugwerk.common.model.ReleaseStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "plugin_release",
    uniqueConstraints = [UniqueConstraint(columnNames = ["plugin_id", "version"])],
)
class PluginReleaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plugin_id", nullable = false, updatable = false)
    var plugin: PluginEntity,

    @Column(name = "version", nullable = false, length = 100)
    var version: String,

    @Column(name = "artifact_sha256", nullable = false, length = 64)
    var artifactSha256: String,

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

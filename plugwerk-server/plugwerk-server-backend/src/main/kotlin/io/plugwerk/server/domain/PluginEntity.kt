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

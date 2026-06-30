package org.openownership.form_builder.model.dao

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.openownership.form_builder.model.dto.WorkspaceDto

@Entity
@Table(name = "workspaces")
class Workspace(
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false, unique = true)
    var slug: String = "",
) : BaseEntity<WorkspaceDto>() {
    constructor(dto: WorkspaceDto) : this(name = dto.name, slug = dto.slug) {
        id = dto.id
        updatedBy = dto.updatedBy
        deletedAt = dto.deletedAt
        deletedBy = dto.deletedBy
    }

    override fun toDto() =
        WorkspaceDto().also {
            it.id = id
            it.name = name
            it.slug = slug
            it.createdAt = createdAt
            it.createdBy = createdBy
            it.updatedAt = updatedAt
            it.updatedBy = updatedBy
            it.deletedAt = deletedAt
            it.deletedBy = deletedBy
        }
}

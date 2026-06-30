package org.openownership.form_builder.model.dao

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.openownership.form_builder.model.dto.FormDto
import java.util.UUID

@Entity
@Table(name = "forms")
class Form(
    @Column(nullable = false)
    var workspaceId: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var title: String = "",

    var description: String? = null,

    var published: Boolean = false,
) : BaseEntity<FormDto>() {

    constructor(dto: FormDto) : this(
        workspaceId = dto.workspaceId,
        title = dto.title,
        description = dto.description,
        published = dto.published,
    ) {
        id = dto.id
        updatedBy = dto.updatedBy
        deletedAt = dto.deletedAt
        deletedBy = dto.deletedBy
    }

    override fun toDto() = FormDto().also {
        it.id = id
        it.workspaceId = workspaceId
        it.title = title
        it.description = description
        it.published = published
        it.createdAt = createdAt
        it.createdBy = createdBy
        it.updatedAt = updatedAt
        it.updatedBy = updatedBy
        it.deletedAt = deletedAt
        it.deletedBy = deletedBy
    }
}
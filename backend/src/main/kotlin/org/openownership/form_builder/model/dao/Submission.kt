package org.openownership.form_builder.model.dao

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.openownership.form_builder.model.dto.SubmissionDto
import java.util.UUID

@Entity
@Table(name = "submissions")
class Submission(
    @Column(nullable = false)
    var formId: UUID = UUID.randomUUID(),

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    var data: Map<String, Any> = emptyMap(),
) : BaseEntity<SubmissionDto>() {

    constructor(dto: SubmissionDto) : this(formId = dto.formId, data = dto.data) {
        id = dto.id
        updatedBy = dto.updatedBy
        deletedAt = dto.deletedAt
        deletedBy = dto.deletedBy
    }

    override fun toDto() = SubmissionDto().also {
        it.id = id
        it.formId = formId
        it.data = data
        it.createdAt = createdAt
        it.createdBy = createdBy
        it.updatedAt = updatedAt
        it.updatedBy = updatedBy
        it.deletedAt = deletedAt
        it.deletedBy = deletedBy
    }
}
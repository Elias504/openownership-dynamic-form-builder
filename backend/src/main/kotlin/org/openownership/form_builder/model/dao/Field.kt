package org.openownership.form_builder.model.dao

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.openownership.form_builder.model.dto.FieldDto
import java.util.UUID

@Entity
@Table(name = "fields")
class Field(
    @Column(nullable = false)
    var formId: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var label: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: FieldType = FieldType.TEXT,

    var required: Boolean = false,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int = 0,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    var config: Map<String, Any> = emptyMap(),
) : BaseEntity<FieldDto>() {

    constructor(dto: FieldDto) : this(
        formId = dto.formId,
        label = dto.label,
        type = dto.type,
        required = dto.required,
        displayOrder = dto.displayOrder,
        config = dto.config,
    ) {
        id = dto.id
        updatedBy = dto.updatedBy
        deletedAt = dto.deletedAt
        deletedBy = dto.deletedBy
    }

    override fun toDto() = FieldDto().also {
        it.id = id
        it.formId = formId
        it.label = label
        it.type = type
        it.required = required
        it.displayOrder = displayOrder
        it.config = config
        it.createdAt = createdAt
        it.createdBy = createdBy
        it.updatedAt = updatedAt
        it.updatedBy = updatedBy
        it.deletedAt = deletedAt
        it.deletedBy = deletedBy
    }
}
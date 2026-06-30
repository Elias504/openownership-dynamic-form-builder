package org.openownership.form_builder.form.field

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "fields")
class Field(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val formId: UUID,

    @Column(nullable = false)
    var label: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: FieldType,

    var required: Boolean = false,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int = 0,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    var config: Map<String, Any> = emptyMap(),
)
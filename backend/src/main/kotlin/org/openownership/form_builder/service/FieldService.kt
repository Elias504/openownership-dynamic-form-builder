package org.openownership.form_builder.service

import org.openownership.form_builder.model.dto.FieldDto
import java.util.UUID

interface FieldService {
    fun findByForm(formId: UUID): List<FieldDto>

    fun findById(id: UUID): FieldDto

    fun save(
        formId: UUID,
        dto: FieldDto,
    ): FieldDto

    fun update(
        id: UUID,
        dto: FieldDto,
    ): FieldDto

    fun delete(id: UUID)
}

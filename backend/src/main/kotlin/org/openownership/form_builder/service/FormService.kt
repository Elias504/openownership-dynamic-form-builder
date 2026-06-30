package org.openownership.form_builder.service

import org.openownership.form_builder.model.dto.FormDto
import java.util.UUID

interface FormService {
    fun findByWorkspace(workspaceId: UUID): List<FormDto>

    fun findById(id: UUID): FormDto

    fun save(
        workspaceId: UUID,
        dto: FormDto,
    ): FormDto

    fun update(
        id: UUID,
        dto: FormDto,
    ): FormDto

    fun delete(id: UUID)
}

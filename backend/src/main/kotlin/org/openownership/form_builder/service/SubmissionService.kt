package org.openownership.form_builder.service

import org.openownership.form_builder.model.dto.SubmissionDto
import java.util.UUID

interface SubmissionService {
    fun findByForm(formId: UUID): List<SubmissionDto>

    fun findById(id: UUID): SubmissionDto

    fun save(
        formId: UUID,
        dto: SubmissionDto,
    ): SubmissionDto
}

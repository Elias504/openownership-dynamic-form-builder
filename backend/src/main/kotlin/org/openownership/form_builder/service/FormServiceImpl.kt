package org.openownership.form_builder.service

import org.openownership.form_builder.model.dao.Form
import org.openownership.form_builder.model.dto.FormDto
import org.openownership.form_builder.repository.FormRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class FormServiceImpl(
    private val repository: FormRepository,
) : FormService {
    @Transactional(readOnly = true)
    override fun findByWorkspace(workspaceId: UUID): List<FormDto> =
        repository.findAllByWorkspaceIdAndDeletedAtIsNull(workspaceId).map { it.toDto() }

    @Transactional(readOnly = true)
    override fun findById(id: UUID): FormDto =
        repository.findByIdAndDeletedAtIsNull(id)?.toDto()
            ?: throw NoSuchElementException("Form $id not found")

    override fun save(
        workspaceId: UUID,
        dto: FormDto,
    ): FormDto {
        val form = Form(dto)
        form.workspaceId = workspaceId
        return repository.save(form).toDto()
    }

    override fun update(
        id: UUID,
        dto: FormDto,
    ): FormDto {
        val existing =
            repository.findByIdAndDeletedAtIsNull(id)
                ?: throw NoSuchElementException("Form $id not found")
        existing.title = dto.title
        existing.description = dto.description
        existing.published = dto.published
        return repository.save(existing).toDto()
    }

    override fun delete(id: UUID) {
        val existing =
            repository.findByIdAndDeletedAtIsNull(id)
                ?: throw NoSuchElementException("Form $id not found")
        existing.deletedAt = Instant.now()
        existing.deletedBy = "system" // TODO: replace with authenticated principal
        repository.save(existing)
    }
}

package org.openownership.form_builder.service

import org.openownership.form_builder.model.dao.Field
import org.openownership.form_builder.model.dto.FieldDto
import org.openownership.form_builder.repository.FieldRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class FieldServiceImpl(
    private val repository: FieldRepository,
) : FieldService {
    @Transactional(readOnly = true)
    override fun findByForm(formId: UUID): List<FieldDto> =
        repository.findAllByFormIdAndDeletedAtIsNullOrderByDisplayOrder(formId).map { it.toDto() }

    @Transactional(readOnly = true)
    override fun findById(id: UUID): FieldDto =
        repository.findByIdAndDeletedAtIsNull(id)?.toDto()
            ?: throw NoSuchElementException("Field $id not found")

    override fun save(
        formId: UUID,
        dto: FieldDto,
    ): FieldDto {
        val field = Field(dto)
        field.formId = formId
        return repository.save(field).toDto()
    }

    override fun update(
        id: UUID,
        dto: FieldDto,
    ): FieldDto {
        val existing =
            repository.findByIdAndDeletedAtIsNull(id)
                ?: throw NoSuchElementException("Field $id not found")
        existing.label = dto.label
        existing.type = dto.type
        existing.required = dto.required
        existing.displayOrder = dto.displayOrder
        existing.config = dto.config
        return repository.save(existing).toDto()
    }

    override fun delete(id: UUID) {
        val existing =
            repository.findByIdAndDeletedAtIsNull(id)
                ?: throw NoSuchElementException("Field $id not found")
        existing.deletedAt = Instant.now()
        existing.deletedBy = "system" // TODO: replace with authenticated principal
        repository.save(existing)
    }
}

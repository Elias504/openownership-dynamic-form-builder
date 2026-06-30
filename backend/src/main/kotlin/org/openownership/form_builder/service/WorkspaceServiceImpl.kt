package org.openownership.form_builder.service

import org.openownership.form_builder.model.dao.Workspace
import org.openownership.form_builder.model.dto.WorkspaceDto
import org.openownership.form_builder.repository.WorkspaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class WorkspaceServiceImpl(
    private val repository: WorkspaceRepository,
) : WorkspaceService {
    @Transactional(readOnly = true)
    override fun findAll(): List<WorkspaceDto> = repository.findAllByDeletedAtIsNullOrderByCreatedAtDesc().map { it.toDto() }

    @Transactional(readOnly = true)
    override fun findById(id: UUID): WorkspaceDto =
        repository.findByIdAndDeletedAtIsNull(id)?.toDto()
            ?: throw NoSuchElementException("Workspace $id not found")

    override fun save(dto: WorkspaceDto): WorkspaceDto = repository.save(Workspace(dto)).toDto()

    override fun update(
        id: UUID,
        dto: WorkspaceDto,
    ): WorkspaceDto {
        val existing =
            repository.findByIdAndDeletedAtIsNull(id)
                ?: throw NoSuchElementException("Workspace $id not found")
        existing.name = dto.name
        existing.slug = dto.slug
        return repository.save(existing).toDto()
    }

    override fun delete(id: UUID) {
        val existing =
            repository.findByIdAndDeletedAtIsNull(id)
                ?: throw NoSuchElementException("Workspace $id not found")
        existing.deletedAt = Instant.now()
        existing.deletedBy = "system" // TODO: replace with authenticated principal
        repository.save(existing)
    }
}

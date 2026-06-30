package org.openownership.form_builder.service

import org.openownership.form_builder.model.dto.WorkspaceDto
import java.util.UUID

interface WorkspaceService {
    fun findAll(): List<WorkspaceDto>
    fun findById(id: UUID): WorkspaceDto
    fun save(dto: WorkspaceDto): WorkspaceDto
    fun update(id: UUID, dto: WorkspaceDto): WorkspaceDto
    fun delete(id: UUID)
}
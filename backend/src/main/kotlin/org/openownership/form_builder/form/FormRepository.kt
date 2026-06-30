package org.openownership.form_builder.form

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FormRepository : JpaRepository<Form, UUID> {
    fun findAllByWorkspaceId(workspaceId: UUID): List<Form>
}
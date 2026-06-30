package org.openownership.form_builder.workspace

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkspaceRepository : JpaRepository<Workspace, UUID> {
    fun findBySlug(slug: String): Workspace?
}
package org.openownership.form_builder.repository

import org.openownership.form_builder.model.dao.Workspace

interface WorkspaceRepository : BaseRepository<Workspace> {
    fun findAllByDeletedAtIsNullOrderByCreatedAtDesc(): List<Workspace>
}

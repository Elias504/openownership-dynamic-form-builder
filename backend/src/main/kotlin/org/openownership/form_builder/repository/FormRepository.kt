package org.openownership.form_builder.repository

import org.openownership.form_builder.model.dao.Form
import java.util.UUID

interface FormRepository : BaseRepository<Form> {
    fun findAllByWorkspaceIdAndDeletedAtIsNull(workspaceId: UUID): List<Form>
}

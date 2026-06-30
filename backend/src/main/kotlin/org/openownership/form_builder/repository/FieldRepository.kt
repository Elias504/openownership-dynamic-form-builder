package org.openownership.form_builder.repository

import org.openownership.form_builder.model.dao.Field
import java.util.UUID

interface FieldRepository : BaseRepository<Field> {
    fun findAllByFormIdAndDeletedAtIsNullOrderByDisplayOrder(formId: UUID): List<Field>
}
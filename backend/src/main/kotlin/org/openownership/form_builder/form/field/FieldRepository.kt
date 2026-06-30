package org.openownership.form_builder.form.field

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FieldRepository : JpaRepository<Field, UUID> {
    fun findAllByFormIdOrderByDisplayOrder(formId: UUID): List<Field>
}
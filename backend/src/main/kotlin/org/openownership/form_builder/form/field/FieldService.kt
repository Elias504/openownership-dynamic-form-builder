package org.openownership.form_builder.form.field

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class FieldService(private val repo: FieldRepository) {

    @Transactional(readOnly = true)
    fun findByForm(formId: UUID): List<Field> =
        repo.findAllByFormIdOrderByDisplayOrder(formId)

    @Transactional(readOnly = true)
    fun findById(id: UUID): Field =
        repo.findById(id).orElseThrow { NoSuchElementException("Field $id not found") }

    fun create(formId: UUID, label: String, type: FieldType, required: Boolean, displayOrder: Int, config: Map<String, Any>): Field =
        repo.save(Field(formId = formId, label = label, type = type, required = required, displayOrder = displayOrder, config = config))

    fun update(id: UUID, label: String, type: FieldType, required: Boolean, displayOrder: Int, config: Map<String, Any>): Field {
        val field = findById(id)
        field.label = label
        field.type = type
        field.required = required
        field.displayOrder = displayOrder
        field.config = config
        return repo.save(field)
    }

    fun delete(id: UUID) = repo.deleteById(id)
}
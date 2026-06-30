package org.openownership.form_builder.model.dto

import org.openownership.form_builder.model.dao.FieldType
import java.util.UUID

class FieldDto : BaseDto() {
    var formId: UUID = UUID.randomUUID()
    var label: String = ""
    var type: FieldType = FieldType.TEXT
    var required: Boolean = false
    var displayOrder: Int = 0
    var config: Map<String, Any> = emptyMap()
}

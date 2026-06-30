package org.openownership.form_builder.model.dto

import java.util.UUID

class SubmissionDto : BaseDto() {
    var formId: UUID = UUID.randomUUID()
    var data: Map<String, Any> = emptyMap()
}

package org.openownership.form_builder.model.dto

import java.util.UUID

class FormDto : BaseDto() {
    var workspaceId: UUID = UUID.randomUUID()
    var title: String = ""
    var description: String? = null
    var published: Boolean = false
}
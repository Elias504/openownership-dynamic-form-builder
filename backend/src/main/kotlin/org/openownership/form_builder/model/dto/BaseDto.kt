package org.openownership.form_builder.model.dto

import java.io.Serializable
import java.time.Instant
import java.util.UUID

abstract class BaseDto : Serializable {
    open var id: UUID = UUID.randomUUID()
    open var createdAt: Instant = Instant.now()
    open var createdBy: String = "system"
    open var updatedAt: Instant = Instant.now()
    open var updatedBy: String = "system"
    open var deletedAt: Instant? = null
    open var deletedBy: String? = null
}

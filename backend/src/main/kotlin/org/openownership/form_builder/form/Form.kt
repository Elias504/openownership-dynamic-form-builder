package org.openownership.form_builder.form

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "forms")
class Form(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val workspaceId: UUID,

    @Column(nullable = false)
    var title: String,

    var description: String? = null,

    var published: Boolean = false,

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
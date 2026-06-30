package org.openownership.form_builder.submission

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "submissions")
class Submission(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val formId: UUID,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    val data: Map<String, Any>,

    @Column(nullable = false, updatable = false)
    val submittedAt: Instant = Instant.now(),
)
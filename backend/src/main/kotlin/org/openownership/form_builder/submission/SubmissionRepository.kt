package org.openownership.form_builder.submission

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SubmissionRepository : JpaRepository<Submission, UUID> {
    fun findAllByFormIdOrderBySubmittedAtDesc(formId: UUID): List<Submission>
}
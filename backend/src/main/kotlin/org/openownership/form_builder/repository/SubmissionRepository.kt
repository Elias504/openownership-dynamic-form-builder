package org.openownership.form_builder.repository

import org.openownership.form_builder.model.dao.Submission
import java.util.UUID

interface SubmissionRepository : BaseRepository<Submission> {
    fun findAllByFormIdAndDeletedAtIsNullOrderByCreatedAtDesc(formId: UUID): List<Submission>
}
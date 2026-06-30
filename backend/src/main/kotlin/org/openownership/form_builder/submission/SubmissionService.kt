package org.openownership.form_builder.submission

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class SubmissionService(private val repo: SubmissionRepository) {

    @Transactional(readOnly = true)
    fun findByForm(formId: UUID): List<Submission> =
        repo.findAllByFormIdOrderBySubmittedAtDesc(formId)

    @Transactional(readOnly = true)
    fun findById(id: UUID): Submission =
        repo.findById(id).orElseThrow { NoSuchElementException("Submission $id not found") }

    fun create(formId: UUID, data: Map<String, Any>): Submission =
        repo.save(Submission(formId = formId, data = data))
}
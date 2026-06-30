package org.openownership.form_builder.service

import org.openownership.form_builder.model.dao.Submission
import org.openownership.form_builder.model.dto.SubmissionDto
import org.openownership.form_builder.repository.SubmissionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class SubmissionServiceImpl(private val repository: SubmissionRepository) : SubmissionService {

    @Transactional(readOnly = true)
    override fun findByForm(formId: UUID): List<SubmissionDto> =
        repository.findAllByFormIdAndDeletedAtIsNullOrderByCreatedAtDesc(formId).map { it.toDto() }

    @Transactional(readOnly = true)
    override fun findById(id: UUID): SubmissionDto =
        repository.findByIdAndDeletedAtIsNull(id)?.toDto()
            ?: throw NoSuchElementException("Submission $id not found")

    override fun save(formId: UUID, dto: SubmissionDto): SubmissionDto {
        val submission = Submission(dto)
        submission.formId = formId
        return repository.save(submission).toDto()
    }
}
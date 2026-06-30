package org.openownership.form_builder.form

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class FormService(private val repo: FormRepository) {

    @Transactional(readOnly = true)
    fun findByWorkspace(workspaceId: UUID): List<Form> =
        repo.findAllByWorkspaceId(workspaceId)

    @Transactional(readOnly = true)
    fun findById(id: UUID): Form =
        repo.findById(id).orElseThrow { NoSuchElementException("Form $id not found") }

    fun create(workspaceId: UUID, title: String, description: String?): Form =
        repo.save(Form(workspaceId = workspaceId, title = title, description = description))

    fun update(id: UUID, title: String, description: String?, published: Boolean): Form {
        val form = findById(id)
        form.title = title
        form.description = description
        form.published = published
        return repo.save(form)
    }

    fun delete(id: UUID) = repo.deleteById(id)
}
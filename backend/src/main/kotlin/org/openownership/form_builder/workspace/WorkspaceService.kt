package org.openownership.form_builder.workspace

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class WorkspaceService(private val repo: WorkspaceRepository) {

    @Transactional(readOnly = true)
    fun findAll(): List<Workspace> = repo.findAll()

    @Transactional(readOnly = true)
    fun findById(id: UUID): Workspace =
        repo.findById(id).orElseThrow { NoSuchElementException("Workspace $id not found") }

    fun create(name: String, slug: String): Workspace =
        repo.save(Workspace(name = name, slug = slug))

    fun update(id: UUID, name: String, slug: String): Workspace {
        val workspace = findById(id)
        workspace.name = name
        workspace.slug = slug
        return repo.save(workspace)
    }

    fun delete(id: UUID) = repo.deleteById(id)
}
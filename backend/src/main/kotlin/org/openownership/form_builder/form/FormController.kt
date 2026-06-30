package org.openownership.form_builder.form

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class FormController(private val service: FormService) {

    @GetMapping("/api/workspaces/{workspaceId}/forms")
    fun listByWorkspace(@PathVariable workspaceId: UUID) =
        service.findByWorkspace(workspaceId)

    @GetMapping("/api/forms/{id}")
    fun get(@PathVariable id: UUID) = service.findById(id)

    @PostMapping("/api/workspaces/{workspaceId}/forms")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@PathVariable workspaceId: UUID, @RequestBody body: FormRequest) =
        service.create(workspaceId, body.title, body.description)

    @PutMapping("/api/forms/{id}")
    fun update(@PathVariable id: UUID, @RequestBody body: FormUpdateRequest) =
        service.update(id, body.title, body.description, body.published)

    @DeleteMapping("/api/forms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) = service.delete(id)
}

data class FormRequest(val title: String, val description: String? = null)
data class FormUpdateRequest(val title: String, val description: String? = null, val published: Boolean = false)
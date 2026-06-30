package org.openownership.form_builder.workspace

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/workspaces")
class WorkspaceController(private val service: WorkspaceService) {

    @GetMapping
    fun list() = service.findAll()

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID) = service.findById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody body: WorkspaceRequest) =
        service.create(body.name, body.slug)

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody body: WorkspaceRequest) =
        service.update(id, body.name, body.slug)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) = service.delete(id)
}

data class WorkspaceRequest(val name: String, val slug: String)
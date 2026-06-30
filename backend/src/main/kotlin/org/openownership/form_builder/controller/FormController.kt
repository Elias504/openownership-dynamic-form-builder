package org.openownership.form_builder.controller

import org.openownership.form_builder.model.dto.FormDto
import org.openownership.form_builder.service.FormService
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
    fun findByWorkspace(@PathVariable workspaceId: UUID) = service.findByWorkspace(workspaceId)

    @GetMapping("/api/forms/{id}")
    fun findById(@PathVariable id: UUID) = service.findById(id)

    @PostMapping("/api/workspaces/{workspaceId}/forms")
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@PathVariable workspaceId: UUID, @RequestBody dto: FormDto) = service.save(workspaceId, dto)

    @PutMapping("/api/forms/{id}")
    fun update(@PathVariable id: UUID, @RequestBody dto: FormDto) = service.update(id, dto)

    @DeleteMapping("/api/forms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) = service.delete(id)
}
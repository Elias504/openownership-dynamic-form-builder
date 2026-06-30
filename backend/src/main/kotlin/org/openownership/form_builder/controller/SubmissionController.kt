package org.openownership.form_builder.controller

import org.openownership.form_builder.model.dto.SubmissionDto
import org.openownership.form_builder.service.SubmissionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class SubmissionController(private val service: SubmissionService) {

    @GetMapping("/api/forms/{formId}/submissions")
    fun findByForm(@PathVariable formId: UUID) = service.findByForm(formId)

    @GetMapping("/api/submissions/{id}")
    fun findById(@PathVariable id: UUID) = service.findById(id)

    @PostMapping("/api/forms/{formId}/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@PathVariable formId: UUID, @RequestBody dto: SubmissionDto) = service.save(formId, dto)
}
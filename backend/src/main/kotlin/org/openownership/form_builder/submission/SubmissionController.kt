package org.openownership.form_builder.submission

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
    fun listByForm(@PathVariable formId: UUID) = service.findByForm(formId)

    @GetMapping("/api/submissions/{id}")
    fun get(@PathVariable id: UUID) = service.findById(id)

    @PostMapping("/api/forms/{formId}/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@PathVariable formId: UUID, @RequestBody body: SubmissionRequest) =
        service.create(formId, body.data)
}

data class SubmissionRequest(val data: Map<String, Any>)
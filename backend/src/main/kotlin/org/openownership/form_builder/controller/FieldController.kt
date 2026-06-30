package org.openownership.form_builder.controller

import org.openownership.form_builder.model.dto.FieldDto
import org.openownership.form_builder.service.FieldService
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
class FieldController(private val service: FieldService) {

    @GetMapping("/api/forms/{formId}/fields")
    fun findByForm(@PathVariable formId: UUID) = service.findByForm(formId)

    @GetMapping("/api/fields/{id}")
    fun findById(@PathVariable id: UUID) = service.findById(id)

    @PostMapping("/api/forms/{formId}/fields")
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@PathVariable formId: UUID, @RequestBody dto: FieldDto) = service.save(formId, dto)

    @PutMapping("/api/fields/{id}")
    fun update(@PathVariable id: UUID, @RequestBody dto: FieldDto) = service.update(id, dto)

    @DeleteMapping("/api/fields/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) = service.delete(id)
}
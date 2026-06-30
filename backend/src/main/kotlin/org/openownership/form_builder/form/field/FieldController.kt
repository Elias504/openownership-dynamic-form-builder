package org.openownership.form_builder.form.field

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
    fun listByForm(@PathVariable formId: UUID) = service.findByForm(formId)

    @PostMapping("/api/forms/{formId}/fields")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@PathVariable formId: UUID, @RequestBody body: FieldRequest) =
        service.create(formId, body.label, body.type, body.required, body.displayOrder, body.config)

    @PutMapping("/api/fields/{id}")
    fun update(@PathVariable id: UUID, @RequestBody body: FieldRequest) =
        service.update(id, body.label, body.type, body.required, body.displayOrder, body.config)

    @DeleteMapping("/api/fields/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) = service.delete(id)
}

data class FieldRequest(
    val label: String,
    val type: FieldType,
    val required: Boolean = false,
    val displayOrder: Int = 0,
    val config: Map<String, Any> = emptyMap(),
)
package org.openownership.form_builder.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.openownership.form_builder.model.dao.Field
import org.openownership.form_builder.model.dao.FieldType
import org.openownership.form_builder.model.dto.FieldDto
import org.openownership.form_builder.repository.FieldRepository
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class FieldServiceImplTest {

    @Mock private lateinit var repository: FieldRepository
    @InjectMocks private lateinit var service: FieldServiceImpl

    private val formId: UUID = UUID.randomUUID()

    private fun field(label: String = "Name", type: FieldType = FieldType.TEXT, order: Int = 0) =
        Field(formId = formId, label = label, type = type, displayOrder = order)

    @Test
    fun `findByForm returns DTOs ordered by displayOrder`() {
        whenever(repository.findAllByFormIdAndDeletedAtIsNullOrderByDisplayOrder(formId))
            .thenReturn(listOf(field("First", order = 0), field("Second", order = 1)))

        val result = service.findByForm(formId)

        assertThat(result).hasSize(2)
        assertThat(result[0].label).isEqualTo("First")
        assertThat(result[0].displayOrder).isEqualTo(0)
        assertThat(result[1].label).isEqualTo("Second")
        assertThat(result[1].displayOrder).isEqualTo(1)
        verify(repository).findAllByFormIdAndDeletedAtIsNullOrderByDisplayOrder(formId)
    }

    @Test
    fun `findById returns DTO when field exists`() {
        val f = field()
        whenever(repository.findByIdAndDeletedAtIsNull(f.id)).thenReturn(f)

        val result = service.findById(f.id)

        assertThat(result.id).isEqualTo(f.id)
        assertThat(result.label).isEqualTo("Name")
        assertThat(result.formId).isEqualTo(formId)
    }

    @Test
    fun `findById throws NoSuchElementException when not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.findById(id) }
    }

    @Test
    fun `save sets formId from path parameter and persists`() {
        val dto = FieldDto().apply { label = "Email"; type = FieldType.TEXT; required = true }
        val saved = field("Email")
        whenever(repository.save(any<Field>())).thenReturn(saved)

        service.save(formId, dto)

        val captor = argumentCaptor<Field>()
        verify(repository).save(captor.capture())
        assertThat(captor.firstValue.formId).isEqualTo(formId)
        assertThat(captor.firstValue.label).isEqualTo("Email")
    }

    @Test
    fun `update modifies label, type, required, displayOrder, and config`() {
        val existing = field()
        whenever(repository.findByIdAndDeletedAtIsNull(existing.id)).thenReturn(existing)
        whenever(repository.save(any<Field>())).thenAnswer { it.arguments[0] as Field }
        val dto = FieldDto().apply {
            label = "Updated Label"
            type = FieldType.NUMBER
            required = true
            displayOrder = 5
            config = mapOf("min" to 0, "max" to 100)
        }

        val result = service.update(existing.id, dto)

        assertThat(result.label).isEqualTo("Updated Label")
        assertThat(result.type).isEqualTo(FieldType.NUMBER)
        assertThat(result.required).isTrue()
        assertThat(result.displayOrder).isEqualTo(5)
        assertThat(result.config).containsEntry("min", 0)
    }

    @Test
    fun `update throws NoSuchElementException when field not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.update(id, FieldDto()) }
    }

    @Test
    fun `delete soft-deletes by setting deletedAt and deletedBy`() {
        val f = field()
        whenever(repository.findByIdAndDeletedAtIsNull(f.id)).thenReturn(f)
        whenever(repository.save(any<Field>())).thenAnswer { it.arguments[0] as Field }

        service.delete(f.id)

        val captor = argumentCaptor<Field>()
        verify(repository).save(captor.capture())
        assertThat(captor.firstValue.deletedAt).isNotNull()
        assertThat(captor.firstValue.deletedBy).isEqualTo("system")
    }

    @Test
    fun `delete throws NoSuchElementException when field not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.delete(id) }
    }
}
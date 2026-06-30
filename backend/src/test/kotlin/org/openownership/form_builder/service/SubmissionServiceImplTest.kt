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
import org.openownership.form_builder.model.dao.Submission
import org.openownership.form_builder.model.dto.SubmissionDto
import org.openownership.form_builder.repository.SubmissionRepository
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SubmissionServiceImplTest {

    @Mock private lateinit var repository: SubmissionRepository
    @InjectMocks private lateinit var service: SubmissionServiceImpl

    private val formId: UUID = UUID.randomUUID()
    private val sampleData = mapOf("field-1" to "Alice", "field-2" to "42")

    private fun submission(data: Map<String, Any> = sampleData) =
        Submission(formId = formId, data = data)

    @Test
    fun `findByForm returns DTOs ordered by createdAt desc`() {
        whenever(repository.findAllByFormIdAndDeletedAtIsNullOrderByCreatedAtDesc(formId))
            .thenReturn(listOf(submission(), submission(mapOf("field-1" to "Bob"))))

        val result = service.findByForm(formId)

        assertThat(result).hasSize(2)
        assertThat(result[0].data["field-1"]).isEqualTo("Alice")
        verify(repository).findAllByFormIdAndDeletedAtIsNullOrderByCreatedAtDesc(formId)
    }

    @Test
    fun `findById returns DTO when submission exists`() {
        val sub = submission()
        whenever(repository.findByIdAndDeletedAtIsNull(sub.id)).thenReturn(sub)

        val result = service.findById(sub.id)

        assertThat(result.id).isEqualTo(sub.id)
        assertThat(result.formId).isEqualTo(formId)
        assertThat(result.data).isEqualTo(sampleData)
    }

    @Test
    fun `findById throws NoSuchElementException when not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.findById(id) }
    }

    @Test
    fun `save sets formId from path parameter and persists data`() {
        val dto = SubmissionDto().apply { data = sampleData }
        val saved = submission()
        whenever(repository.save(any<Submission>())).thenReturn(saved)

        val result = service.save(formId, dto)

        val captor = argumentCaptor<Submission>()
        verify(repository).save(captor.capture())
        assertThat(captor.firstValue.formId).isEqualTo(formId)
        assertThat(captor.firstValue.data).isEqualTo(sampleData)
        assertThat(result.formId).isEqualTo(formId)
    }
}
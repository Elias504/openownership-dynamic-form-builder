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
import org.openownership.form_builder.model.dao.Form
import org.openownership.form_builder.model.dto.FormDto
import org.openownership.form_builder.repository.FormRepository
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class FormServiceImplTest {
    @Mock private lateinit var repository: FormRepository

    @InjectMocks private lateinit var service: FormServiceImpl

    private val workspaceId: UUID = UUID.randomUUID()

    private fun form(
        title: String = "Test Form",
        published: Boolean = false,
    ) = Form(workspaceId = workspaceId, title = title, published = published)

    @Test
    fun `findByWorkspace returns DTOs filtered by workspaceId`() {
        whenever(repository.findAllByWorkspaceIdAndDeletedAtIsNull(workspaceId))
            .thenReturn(listOf(form("Form A"), form("Form B")))

        val result = service.findByWorkspace(workspaceId)

        assertThat(result).hasSize(2)
        assertThat(result.map { it.title }).containsExactly("Form A", "Form B")
        verify(repository).findAllByWorkspaceIdAndDeletedAtIsNull(workspaceId)
    }

    @Test
    fun `findById returns DTO when form exists`() {
        val f = form()
        whenever(repository.findByIdAndDeletedAtIsNull(f.id)).thenReturn(f)

        val result = service.findById(f.id)

        assertThat(result.id).isEqualTo(f.id)
        assertThat(result.title).isEqualTo("Test Form")
        assertThat(result.workspaceId).isEqualTo(workspaceId)
    }

    @Test
    fun `findById throws NoSuchElementException when not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.findById(id) }
    }

    @Test
    fun `save sets workspaceId from path parameter and persists`() {
        val dto =
            FormDto().apply {
                title = "New Form"
                description = "Desc"
            }
        val saved = form("New Form")
        whenever(repository.save(any<Form>())).thenReturn(saved)

        val result = service.save(workspaceId, dto)

        val captor = argumentCaptor<Form>()
        verify(repository).save(captor.capture())
        assertThat(captor.firstValue.workspaceId).isEqualTo(workspaceId)
        assertThat(result.title).isEqualTo("New Form")
    }

    @Test
    fun `update modifies title, description, and published`() {
        val existing = form()
        whenever(repository.findByIdAndDeletedAtIsNull(existing.id)).thenReturn(existing)
        whenever(repository.save(any<Form>())).thenAnswer { it.arguments[0] as Form }
        val dto =
            FormDto().apply {
                title = "Updated"
                description = "New desc"
                published = true
            }

        val result = service.update(existing.id, dto)

        assertThat(result.title).isEqualTo("Updated")
        assertThat(result.description).isEqualTo("New desc")
        assertThat(result.published).isTrue()
    }

    @Test
    fun `update throws NoSuchElementException when form not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.update(id, FormDto()) }
    }

    @Test
    fun `delete soft-deletes by setting deletedAt and deletedBy`() {
        val f = form()
        whenever(repository.findByIdAndDeletedAtIsNull(f.id)).thenReturn(f)
        whenever(repository.save(any<Form>())).thenAnswer { it.arguments[0] as Form }

        service.delete(f.id)

        val captor = argumentCaptor<Form>()
        verify(repository).save(captor.capture())
        assertThat(captor.firstValue.deletedAt).isNotNull()
        assertThat(captor.firstValue.deletedBy).isEqualTo("system")
    }

    @Test
    fun `delete throws NoSuchElementException when form not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.delete(id) }
    }
}

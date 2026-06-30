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
import org.openownership.form_builder.model.dao.Workspace
import org.openownership.form_builder.model.dto.WorkspaceDto
import org.openownership.form_builder.repository.WorkspaceRepository
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class WorkspaceServiceImplTest {
    @Mock private lateinit var repository: WorkspaceRepository

    @InjectMocks private lateinit var service: WorkspaceServiceImpl

    private fun workspace(
        name: String = "Test",
        slug: String = "test",
    ) = Workspace(name = name, slug = slug)

    @Test
    fun `findAll returns DTOs for all non-deleted workspaces`() {
        whenever(repository.findAllByDeletedAtIsNullOrderByCreatedAtDesc())
            .thenReturn(listOf(workspace("ACME", "acme"), workspace("Beta", "beta")))

        val result = service.findAll()

        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactly("ACME", "Beta")
        verify(repository).findAllByDeletedAtIsNullOrderByCreatedAtDesc()
    }

    @Test
    fun `findById returns DTO when workspace exists`() {
        val ws = workspace()
        whenever(repository.findByIdAndDeletedAtIsNull(ws.id)).thenReturn(ws)

        val result = service.findById(ws.id)

        assertThat(result.id).isEqualTo(ws.id)
        assertThat(result.name).isEqualTo("Test")
        assertThat(result.slug).isEqualTo("test")
    }

    @Test
    fun `findById throws NoSuchElementException when not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.findById(id) }
    }

    @Test
    fun `save persists workspace and returns DTO`() {
        val dto =
            WorkspaceDto().apply {
                name = "New WS"
                slug = "new-ws"
            }
        val saved = workspace("New WS", "new-ws")
        whenever(repository.save(any<Workspace>())).thenReturn(saved)

        val result = service.save(dto)

        assertThat(result.name).isEqualTo("New WS")
        assertThat(result.slug).isEqualTo("new-ws")
    }

    @Test
    fun `update modifies name and slug on existing workspace`() {
        val existing = workspace()
        whenever(repository.findByIdAndDeletedAtIsNull(existing.id)).thenReturn(existing)
        whenever(repository.save(any<Workspace>())).thenAnswer { it.arguments[0] as Workspace }
        val dto =
            WorkspaceDto().apply {
                name = "Renamed"
                slug = "renamed"
            }

        val result = service.update(existing.id, dto)

        assertThat(result.name).isEqualTo("Renamed")
        assertThat(result.slug).isEqualTo("renamed")
    }

    @Test
    fun `update throws NoSuchElementException when workspace not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.update(id, WorkspaceDto()) }
    }

    @Test
    fun `delete sets deletedAt and deletedBy without hard-deleting`() {
        val ws = workspace()
        whenever(repository.findByIdAndDeletedAtIsNull(ws.id)).thenReturn(ws)
        whenever(repository.save(any<Workspace>())).thenAnswer { it.arguments[0] as Workspace }

        service.delete(ws.id)

        val captor = argumentCaptor<Workspace>()
        verify(repository).save(captor.capture())
        assertThat(captor.firstValue.deletedAt).isNotNull()
        assertThat(captor.firstValue.deletedBy).isEqualTo("system")
    }

    @Test
    fun `delete throws NoSuchElementException when workspace not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findByIdAndDeletedAtIsNull(id)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.delete(id) }
    }
}

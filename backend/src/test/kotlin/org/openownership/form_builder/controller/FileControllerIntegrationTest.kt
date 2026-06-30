package org.openownership.form_builder.controller

import org.hamcrest.Matchers.matchesPattern
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.openownership.form_builder.AbstractIntegrationTest
import org.openownership.form_builder.service.FileService
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.ByteArrayInputStream

class FileControllerIntegrationTest : AbstractIntegrationTest() {
    @MockitoBean
    private lateinit var fileService: FileService

    @Test
    fun `POST upload returns 201 with object key`() {
        whenever(fileService.upload(any())).thenReturn("submissions/abc-123/report.pdf")

        val file = MockMultipartFile("file", "report.pdf", "application/pdf", "content".toByteArray())

        mockMvc
            .perform(multipart("/api/files/upload").file(file))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.key").value("submissions/abc-123/report.pdf"))
    }

    @Test
    fun `POST upload key follows submissions-uuid-filename pattern`() {
        whenever(fileService.upload(any())).thenAnswer { invocation ->
            val mf = invocation.getArgument<org.springframework.web.multipart.MultipartFile>(0)
            "submissions/550e8400-e29b-41d4-a716-446655440000/${mf.originalFilename}"
        }

        val file = MockMultipartFile("file", "document.txt", "text/plain", "hello".toByteArray())

        mockMvc
            .perform(multipart("/api/files/upload").file(file))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.key").value(matchesPattern("submissions/.+/document\\.txt")))
    }

    @Test
    fun `GET download returns file content with attachment header`() {
        val key = "submissions/abc-123/report.pdf"
        whenever(fileService.download(key)).thenReturn(ByteArrayInputStream("file content".toByteArray()))

        mockMvc
            .perform(get("/api/files/download").param("key", key))
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"report.pdf\""))
            .andExpect(header().string("Content-Type", "application/octet-stream"))
    }
}

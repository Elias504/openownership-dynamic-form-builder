package org.openownership.form_builder.controller

import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.openownership.form_builder.AbstractIntegrationTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class WorkspaceControllerIntegrationTest : AbstractIntegrationTest() {
    private fun createWorkspace(
        name: String = "Test WS",
        slug: String = "test-ws",
    ): String {
        val result =
            mockMvc
                .perform(
                    post("/api/workspaces")
                        .contentType(APPLICATION_JSON)
                        .content("""{"name":"$name","slug":"$slug"}"""),
                ).andExpect(status().isCreated)
                .andReturn()
        return objectMapper.readTree(result.response.contentAsString)["id"].asText()
    }

    @Test
    fun `POST returns 201 with full DTO`() {
        mockMvc
            .perform(
                post("/api/workspaces")
                    .contentType(APPLICATION_JSON)
                    .content("""{"name":"ACME","slug":"acme"}"""),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isString)
            .andExpect(jsonPath("$.name").value("ACME"))
            .andExpect(jsonPath("$.slug").value("acme"))
            .andExpect(jsonPath("$.createdAt").isString)
            .andExpect(jsonPath("$.createdBy").value("system"))
            .andExpect(jsonPath("$.deletedAt").value(null as Any?))
    }

    @Test
    fun `GET list returns all non-deleted workspaces`() {
        createWorkspace("WS One", "ws-one")
        createWorkspace("WS Two", "ws-two")

        mockMvc
            .perform(get("/api/workspaces"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].name").value("WS Two")) // ordered by createdAt desc
            .andExpect(jsonPath("$[1].name").value("WS One"))
    }

    @Test
    fun `GET by ID returns the workspace`() {
        val id = createWorkspace("Lookup WS", "lookup-ws")

        mockMvc
            .perform(get("/api/workspaces/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Lookup WS"))
            .andExpect(jsonPath("$.slug").value("lookup-ws"))
    }

    @Test
    fun `PUT updates name and slug`() {
        val id = createWorkspace()

        mockMvc
            .perform(
                put("/api/workspaces/$id")
                    .contentType(APPLICATION_JSON)
                    .content("""{"name":"Renamed WS","slug":"renamed-ws"}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Renamed WS"))
            .andExpect(jsonPath("$.slug").value("renamed-ws"))
    }

    @Test
    fun `DELETE returns 204 and workspace is excluded from subsequent list`() {
        val id = createWorkspace()

        mockMvc
            .perform(delete("/api/workspaces/$id"))
            .andExpect(status().isNoContent)

        mockMvc
            .perform(get("/api/workspaces"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(0)))
    }
}

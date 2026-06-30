package org.openownership.form_builder.controller

import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openownership.form_builder.AbstractIntegrationTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FormControllerIntegrationTest : AbstractIntegrationTest() {
    private lateinit var workspaceId: String

    @BeforeEach
    fun setup() {
        val result =
            mockMvc
                .perform(
                    post("/api/workspaces")
                        .contentType(APPLICATION_JSON)
                        .content("""{"name":"Test WS","slug":"test-ws"}"""),
                ).andReturn()
        workspaceId = objectMapper.readTree(result.response.contentAsString)["id"].asText()
    }

    private fun createForm(
        title: String = "My Form",
        description: String? = null,
    ): String {
        val body =
            if (description != null) {
                """{"title":"$title","description":"$description"}"""
            } else {
                """{"title":"$title"}"""
            }
        val result =
            mockMvc
                .perform(
                    post("/api/workspaces/$workspaceId/forms")
                        .contentType(APPLICATION_JSON)
                        .content(body),
                ).andExpect(status().isCreated)
                .andReturn()
        return objectMapper.readTree(result.response.contentAsString)["id"].asText()
    }

    @Test
    fun `POST creates form under workspace and returns 201 with full DTO`() {
        mockMvc
            .perform(
                post("/api/workspaces/$workspaceId/forms")
                    .contentType(APPLICATION_JSON)
                    .content("""{"title":"Registration","description":"Sign-up form"}"""),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isString)
            .andExpect(jsonPath("$.workspaceId").value(workspaceId))
            .andExpect(jsonPath("$.title").value("Registration"))
            .andExpect(jsonPath("$.description").value("Sign-up form"))
            .andExpect(jsonPath("$.published").value(false))
            .andExpect(jsonPath("$.createdAt").isString)
            .andExpect(jsonPath("$.deletedAt").value(null as Any?))
    }

    @Test
    fun `GET list returns all non-deleted forms for workspace`() {
        createForm("Form A")
        createForm("Form B")

        mockMvc
            .perform(get("/api/workspaces/$workspaceId/forms"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
    }

    @Test
    fun `GET by ID returns the form`() {
        val id = createForm("Lookup Form")

        mockMvc
            .perform(get("/api/forms/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.title").value("Lookup Form"))
            .andExpect(jsonPath("$.workspaceId").value(workspaceId))
    }

    @Test
    fun `PUT updates title, description, and published`() {
        val id = createForm()

        mockMvc
            .perform(
                put("/api/forms/$id")
                    .contentType(APPLICATION_JSON)
                    .content("""{"title":"Updated","description":"New desc","published":true}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated"))
            .andExpect(jsonPath("$.description").value("New desc"))
            .andExpect(jsonPath("$.published").value(true))
    }

    @Test
    fun `DELETE returns 204 and form is excluded from subsequent list`() {
        val id = createForm()

        mockMvc
            .perform(delete("/api/forms/$id"))
            .andExpect(status().isNoContent)

        mockMvc
            .perform(get("/api/workspaces/$workspaceId/forms"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(0)))
    }
}

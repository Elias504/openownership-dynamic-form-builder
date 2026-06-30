package org.openownership.form_builder.controller

import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openownership.form_builder.AbstractIntegrationTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SubmissionControllerIntegrationTest : AbstractIntegrationTest() {

    private lateinit var formId: String
    private lateinit var fieldId: String

    @BeforeEach
    fun setup() {
        val wsResult = mockMvc.perform(post("/api/workspaces")
            .contentType(APPLICATION_JSON)
            .content("""{"name":"Test WS","slug":"test-ws"}"""))
            .andReturn()
        val workspaceId = objectMapper.readTree(wsResult.response.contentAsString)["id"].asText()

        val formResult = mockMvc.perform(post("/api/workspaces/$workspaceId/forms")
            .contentType(APPLICATION_JSON)
            .content("""{"title":"Test Form"}"""))
            .andReturn()
        formId = objectMapper.readTree(formResult.response.contentAsString)["id"].asText()

        val fieldResult = mockMvc.perform(post("/api/forms/$formId/fields")
            .contentType(APPLICATION_JSON)
            .content("""{"label":"Name","type":"TEXT","required":true,"displayOrder":0}"""))
            .andReturn()
        fieldId = objectMapper.readTree(fieldResult.response.contentAsString)["id"].asText()
    }

    @Test
    fun `POST creates submission and returns 201 with full DTO`() {
        mockMvc.perform(post("/api/forms/$formId/submissions")
            .contentType(APPLICATION_JSON)
            .content("""{"data":{"$fieldId":"Alice"}}"""))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isString)
            .andExpect(jsonPath("$.formId").value(formId))
            .andExpect(jsonPath("$.data.$fieldId").value("Alice"))
            .andExpect(jsonPath("$.createdAt").isString)
            .andExpect(jsonPath("$.createdBy").value("system"))
            .andExpect(jsonPath("$.deletedAt").value(null as Any?))
    }

    @Test
    fun `GET list returns submissions for form ordered by createdAt desc`() {
        mockMvc.perform(post("/api/forms/$formId/submissions")
            .contentType(APPLICATION_JSON)
            .content("""{"data":{"$fieldId":"Alice"}}"""))
            .andExpect(status().isCreated)
        mockMvc.perform(post("/api/forms/$formId/submissions")
            .contentType(APPLICATION_JSON)
            .content("""{"data":{"$fieldId":"Bob"}}"""))
            .andExpect(status().isCreated)

        mockMvc.perform(get("/api/forms/$formId/submissions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
    }

    @Test
    fun `GET by ID returns the submission`() {
        val result = mockMvc.perform(post("/api/forms/$formId/submissions")
            .contentType(APPLICATION_JSON)
            .content("""{"data":{"$fieldId":"Charlie"}}"""))
            .andExpect(status().isCreated)
            .andReturn()
        val id = objectMapper.readTree(result.response.contentAsString)["id"].asText()

        mockMvc.perform(get("/api/submissions/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.formId").value(formId))
            .andExpect(jsonPath("$.data.$fieldId").value("Charlie"))
    }

    @Test
    fun `GET list returns empty when no submissions exist`() {
        mockMvc.perform(get("/api/forms/$formId/submissions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(0)))
    }
}
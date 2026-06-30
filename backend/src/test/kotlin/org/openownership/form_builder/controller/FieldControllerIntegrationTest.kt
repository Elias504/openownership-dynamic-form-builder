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

class FieldControllerIntegrationTest : AbstractIntegrationTest() {
    private lateinit var formId: String

    @BeforeEach
    fun setup() {
        val wsResult =
            mockMvc
                .perform(
                    post("/api/workspaces")
                        .contentType(APPLICATION_JSON)
                        .content("""{"name":"Test WS","slug":"test-ws"}"""),
                ).andReturn()
        val workspaceId = objectMapper.readTree(wsResult.response.contentAsString)["id"].asText()

        val formResult =
            mockMvc
                .perform(
                    post("/api/workspaces/$workspaceId/forms")
                        .contentType(APPLICATION_JSON)
                        .content("""{"title":"Test Form"}"""),
                ).andReturn()
        formId = objectMapper.readTree(formResult.response.contentAsString)["id"].asText()
    }

    private fun createField(
        label: String = "Name",
        type: String = "TEXT",
        order: Int = 0,
    ): String {
        val result =
            mockMvc
                .perform(
                    post("/api/forms/$formId/fields")
                        .contentType(APPLICATION_JSON)
                        .content("""{"label":"$label","type":"$type","required":false,"displayOrder":$order}"""),
                ).andExpect(status().isCreated)
                .andReturn()
        return objectMapper.readTree(result.response.contentAsString)["id"].asText()
    }

    @Test
    fun `POST creates field under form and returns 201 with full DTO`() {
        mockMvc
            .perform(
                post("/api/forms/$formId/fields")
                    .contentType(APPLICATION_JSON)
                    .content("""{"label":"Email","type":"TEXT","required":true,"displayOrder":0}"""),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isString)
            .andExpect(jsonPath("$.formId").value(formId))
            .andExpect(jsonPath("$.label").value("Email"))
            .andExpect(jsonPath("$.type").value("TEXT"))
            .andExpect(jsonPath("$.required").value(true))
            .andExpect(jsonPath("$.displayOrder").value(0))
            .andExpect(jsonPath("$.createdAt").isString)
            .andExpect(jsonPath("$.deletedAt").value(null as Any?))
    }

    @Test
    fun `GET list returns fields ordered by displayOrder`() {
        createField("First", order = 0)
        createField("Second", order = 1)
        createField("Third", order = 2)

        mockMvc
            .perform(get("/api/forms/$formId/fields"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(3)))
            .andExpect(jsonPath("$[0].label").value("First"))
            .andExpect(jsonPath("$[1].label").value("Second"))
            .andExpect(jsonPath("$[2].label").value("Third"))
    }

    @Test
    fun `GET by ID returns the field`() {
        val id = createField("Phone")

        mockMvc
            .perform(get("/api/fields/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.label").value("Phone"))
            .andExpect(jsonPath("$.formId").value(formId))
    }

    @Test
    fun `PUT updates label, type, required, and displayOrder`() {
        val id = createField()

        mockMvc
            .perform(
                put("/api/fields/$id")
                    .contentType(APPLICATION_JSON)
                    .content("""{"label":"Full Name","type":"TEXTAREA","required":true,"displayOrder":3,"config":{}}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.label").value("Full Name"))
            .andExpect(jsonPath("$.type").value("TEXTAREA"))
            .andExpect(jsonPath("$.required").value(true))
            .andExpect(jsonPath("$.displayOrder").value(3))
    }

    @Test
    fun `DELETE returns 204 and field is excluded from subsequent list`() {
        val id = createField()

        mockMvc
            .perform(delete("/api/fields/$id"))
            .andExpect(status().isNoContent)

        mockMvc
            .perform(get("/api/forms/$formId/fields"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(0)))
    }
}

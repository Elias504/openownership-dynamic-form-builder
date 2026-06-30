package org.openownership.form_builder

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
abstract class AbstractIntegrationTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
}
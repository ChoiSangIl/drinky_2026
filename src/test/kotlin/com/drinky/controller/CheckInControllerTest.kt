package com.drinky.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CheckInControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @Test
    fun `unauthenticated user cannot access checkin post endpoint`() {
        mockMvc.perform(post("/checkin").param("isSober", "true").with(csrf()))
            .andExpect(status().is3xxRedirection)
    }

    @Test
    fun `unauthenticated user cannot get today checkin`() {
        mockMvc.perform(get("/checkin/today"))
            .andExpect(status().is3xxRedirection)
    }
}

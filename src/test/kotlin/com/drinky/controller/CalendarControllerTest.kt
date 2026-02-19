package com.drinky.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CalendarControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @Test
    fun `unauthenticated user cannot access calendar page`() {
        mockMvc.perform(get("/calendar"))
            .andExpect(status().is3xxRedirection)
    }

    @Test
    fun `unauthenticated user cannot access month calendar`() {
        mockMvc.perform(get("/calendar/2026/2"))
            .andExpect(status().is3xxRedirection)
    }
}

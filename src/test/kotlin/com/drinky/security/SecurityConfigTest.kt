package com.drinky.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @Test
    fun `health endpoint should be accessible without authentication`() {
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk)
    }

    @Test
    fun `root endpoint should be accessible without authentication`() {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
    }

    @Test
    fun `protected endpoint should redirect to login`() {
        mockMvc.perform(get("/dashboard"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrlPattern("**/login"))
    }

    @Test
    fun `login endpoint should be accessible without authentication`() {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk)
    }
}

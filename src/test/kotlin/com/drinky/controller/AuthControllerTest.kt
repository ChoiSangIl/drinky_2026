package com.drinky.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @Test
    fun `login page is accessible without authentication`() {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk)
            .andExpect(view().name("pages/login"))
    }

    @Test
    fun `onboarding page is accessible without authentication`() {
        mockMvc.perform(get("/onboarding"))
            .andExpect(status().isOk)
            .andExpect(view().name("pages/onboarding"))
    }

    @Test
    @WithMockUser
    fun `authenticated user is redirected from login to home`() {
        mockMvc.perform(get("/login"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/"))
    }

    @Test
    @WithMockUser
    fun `authenticated user is redirected from onboarding to home`() {
        mockMvc.perform(get("/onboarding"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/"))
    }

    @Test
    fun `unauthenticated user accessing protected page is redirected to login`() {
        mockMvc.perform(get("/dashboard"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrlPattern("**/login"))
    }
}

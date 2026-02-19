package com.drinky.controller

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthController {

    @GetMapping("/login")
    fun loginPage(authentication: Authentication?): String {
        if (authentication != null && authentication.isAuthenticated) {
            return "redirect:/"
        }
        return "pages/login"
    }

    @GetMapping("/onboarding")
    fun onboardingPage(authentication: Authentication?): String {
        if (authentication != null && authentication.isAuthenticated) {
            return "redirect:/"
        }
        return "pages/onboarding"
    }
}

package com.drinky.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2SuccessHandler : SimpleUrlAuthenticationSuccessHandler() {

    init {
        defaultTargetUrl = "/"
        isAlwaysUseDefaultTargetUrl = false
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        if (response.isCommitted) return
        super.onAuthenticationSuccess(request, response, authentication)
    }
}

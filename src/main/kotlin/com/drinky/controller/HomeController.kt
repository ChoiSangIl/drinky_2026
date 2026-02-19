package com.drinky.controller

import com.drinky.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDateTime

@Controller
class HomeController {

    @GetMapping("/health")
    @ResponseBody
    fun health(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(
            mapOf(
                "status" to "UP",
                "service" to "drinky",
                "timestamp" to LocalDateTime.now().toString()
            )
        )
    }

    @GetMapping("/")
    fun home(@AuthenticationPrincipal principal: UserPrincipal?, model: Model): String {
        if (principal != null) {
            model.addAttribute("user", mapOf(
                "id" to principal.id,
                "nickname" to principal.nickname,
                "email" to principal.email,
                "profileImage" to principal.profileImage
            ))
        }
        return "pages/home"
    }
}

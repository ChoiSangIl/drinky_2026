package com.drinky.controller

import com.drinky.security.UserPrincipal
import com.drinky.service.CheckInService
import com.drinky.util.EncouragementMessages
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/checkin")
class CheckInController(
    private val checkInService: CheckInService
) {

    @PostMapping
    fun checkIn(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam isSober: Boolean,
        model: Model,
        response: HttpServletResponse
    ): String {
        val checkIn = checkInService.checkIn(principal.id, isSober)
        val message = EncouragementMessages.getRandom(isSober)

        model.addAttribute("checkIn", checkIn)
        model.addAttribute("message", message)

        // 스트릭 영역 업데이트 트리거
        response.setHeader("HX-Trigger", "streakUpdated")

        return "fragments/checkin-result"
    }

    @GetMapping("/today")
    fun getTodayCheckIn(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam(required = false) edit: Boolean?,
        model: Model
    ): String {
        val checkIn = checkInService.getTodayCheckIn(principal.id)

        return if (checkIn != null && edit != true) {
            model.addAttribute("checkIn", checkIn)
            model.addAttribute("message", EncouragementMessages.getRandom(checkIn.isSober))
            "fragments/checkin-result"
        } else {
            "fragments/checkin-buttons"
        }
    }
}

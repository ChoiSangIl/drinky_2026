package com.drinky.controller

import com.drinky.security.UserPrincipal
import com.drinky.service.CheckInService
import com.drinky.util.EncouragementMessages
import jakarta.servlet.http.HttpServletResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

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

        model.addAttribute("checkIn", checkIn)
        model.addAttribute("edit", false)
        model.addAttribute("message", EncouragementMessages.getRandom(isSober))

        response.setHeader("HX-Trigger", "streakUpdated")

        return "fragments/checkin-area"
    }

    @PostMapping("/{date}")
    fun checkInForDate(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        @RequestParam isSober: Boolean,
        model: Model,
        response: HttpServletResponse
    ): String {
        if (!checkInService.isEditable(date)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "7일이 지난 기록은 수정할 수 없습니다.")
        }

        val checkIn = checkInService.checkInForDate(principal.id, date, isSober)

        model.addAttribute("date", date)
        model.addAttribute("checkIn", checkIn)
        model.addAttribute("editable", true)
        model.addAttribute("message", "기록이 수정되었습니다.")

        response.setHeader("HX-Trigger", "calendarUpdated, streakUpdated")

        return "fragments/day-detail"
    }

    @GetMapping("/today")
    fun getTodayCheckIn(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam(required = false) edit: Boolean?,
        model: Model
    ): String {
        val checkIn = checkInService.getTodayCheckIn(principal.id)

        model.addAttribute("checkIn", checkIn)
        model.addAttribute("edit", edit ?: false)
        if (checkIn != null) {
            model.addAttribute("message", EncouragementMessages.getRandom(checkIn.isSober))
        }

        return "fragments/checkin-area"
    }
}

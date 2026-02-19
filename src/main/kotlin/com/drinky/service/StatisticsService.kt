package com.drinky.service

import com.drinky.domain.dto.MonthlyStatDto
import com.drinky.domain.dto.SummaryStatDto
import com.drinky.domain.dto.WeeklyStatDto
import com.drinky.repository.CheckInRepository
import com.drinky.repository.StreakRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.util.UUID

@Service
@Transactional(readOnly = true)
class StatisticsService(
    private val checkInRepository: CheckInRepository,
    private val streakRepository: StreakRepository
) {

    fun getWeeklyStats(userId: UUID): List<WeeklyStatDto> {
        val today = LocalDate.now()
        val thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val startDate = thisMonday.minusWeeks(3)

        val checkIns = checkInRepository.findByUserIdAndCheckDateBetween(userId, startDate, today)

        return (3 downTo 0).map { weeksAgo ->
            val weekStart = thisMonday.minusWeeks(weeksAgo.toLong())
            val weekEnd = weekStart.plusDays(6).let { if (it.isAfter(today)) today else it }
            val weekCheckIns = checkIns.filter { it.checkDate in weekStart..weekEnd }

            WeeklyStatDto(
                weekLabel = if (weeksAgo == 0) "이번 주" else "${weeksAgo}주 전",
                drinkingDays = weekCheckIns.count { !it.isSober },
                soberDays = weekCheckIns.count { it.isSober }
            )
        }
    }

    fun getMonthlyStats(userId: UUID): List<MonthlyStatDto> {
        val today = LocalDate.now()
        val currentMonth = YearMonth.from(today)

        return (2 downTo 0).map { monthsAgo ->
            val month = currentMonth.minusMonths(monthsAgo.toLong())
            val startDate = month.atDay(1)
            val endDate = month.atEndOfMonth().let { if (it.isAfter(today)) today else it }

            val checkIns = checkInRepository.findByUserIdAndCheckDateBetween(userId, startDate, endDate)

            MonthlyStatDto(
                monthLabel = "${month.monthValue}월",
                drinkingDays = checkIns.count { !it.isSober },
                soberDays = checkIns.count { it.isSober }
            )
        }
    }

    fun getSummaryStats(userId: UUID): SummaryStatDto {
        val today = LocalDate.now()
        val thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val lastMonday = thisMonday.minusWeeks(1)
        val thisMonthStart = today.withDayOfMonth(1)
        val lastMonthStart = thisMonthStart.minusMonths(1)
        val lastMonthEnd = thisMonthStart.minusDays(1)

        val thisWeekCheckIns = checkInRepository.findByUserIdAndCheckDateBetween(userId, thisMonday, today)
        val lastWeekCheckIns = checkInRepository.findByUserIdAndCheckDateBetween(userId, lastMonday, thisMonday.minusDays(1))
        val thisMonthCheckIns = checkInRepository.findByUserIdAndCheckDateBetween(userId, thisMonthStart, today)
        val lastMonthCheckIns = checkInRepository.findByUserIdAndCheckDateBetween(userId, lastMonthStart, lastMonthEnd)

        val thisWeekDrinking = thisWeekCheckIns.count { !it.isSober }
        val lastWeekDrinking = lastWeekCheckIns.count { !it.isSober }
        val thisMonthDrinking = thisMonthCheckIns.count { !it.isSober }
        val lastMonthDrinking = lastMonthCheckIns.count { !it.isSober }

        val streak = streakRepository.findByUserId(userId)

        return SummaryStatDto(
            thisWeekDrinking = thisWeekDrinking,
            lastWeekDrinking = lastWeekDrinking,
            weekChange = thisWeekDrinking - lastWeekDrinking,
            thisMonthDrinking = thisMonthDrinking,
            lastMonthDrinking = lastMonthDrinking,
            monthChange = thisMonthDrinking - lastMonthDrinking,
            currentStreak = streak?.currentStreak ?: 0,
            longestStreak = streak?.longestStreak ?: 0
        )
    }
}

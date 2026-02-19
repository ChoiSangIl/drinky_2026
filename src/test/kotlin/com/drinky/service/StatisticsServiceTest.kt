package com.drinky.service

import com.drinky.domain.entity.CheckIn
import com.drinky.domain.entity.Streak
import com.drinky.repository.CheckInRepository
import com.drinky.repository.StreakRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.UUID

class StatisticsServiceTest {

    private lateinit var checkInRepository: CheckInRepository
    private lateinit var streakRepository: StreakRepository
    private lateinit var statisticsService: StatisticsService

    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        checkInRepository = mockk()
        streakRepository = mockk()
        statisticsService = StatisticsService(checkInRepository, streakRepository)
    }

    @Test
    fun `getWeeklyStats returns 4 weeks of data`() {
        every { checkInRepository.findByUserIdAndCheckDateBetween(userId, any(), any()) } returns emptyList()

        val result = statisticsService.getWeeklyStats(userId)

        assertThat(result).hasSize(4)
        assertThat(result.last().weekLabel).isEqualTo("이번 주")
        assertThat(result.first().weekLabel).isEqualTo("3주 전")
    }

    @Test
    fun `getMonthlyStats returns 3 months of data`() {
        every { checkInRepository.findByUserIdAndCheckDateBetween(userId, any(), any()) } returns emptyList()

        val result = statisticsService.getMonthlyStats(userId)

        assertThat(result).hasSize(3)
    }

    @Test
    fun `getSummaryStats calculates week change correctly`() {
        val today = LocalDate.now()
        val thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        // 이번 주: 음주 1일
        every {
            checkInRepository.findByUserIdAndCheckDateBetween(userId, thisMonday, today)
        } returns listOf(
            CheckIn(userId = userId, checkDate = thisMonday, isSober = false)
        )

        // 지난 주: 음주 3일
        every {
            checkInRepository.findByUserIdAndCheckDateBetween(userId, thisMonday.minusWeeks(1), thisMonday.minusDays(1))
        } returns listOf(
            CheckIn(userId = userId, checkDate = thisMonday.minusDays(1), isSober = false),
            CheckIn(userId = userId, checkDate = thisMonday.minusDays(2), isSober = false),
            CheckIn(userId = userId, checkDate = thisMonday.minusDays(3), isSober = false)
        )

        // 이번 달 / 지난 달
        every {
            checkInRepository.findByUserIdAndCheckDateBetween(userId, today.withDayOfMonth(1), today)
        } returns listOf(
            CheckIn(userId = userId, checkDate = today, isSober = false)
        )
        every {
            checkInRepository.findByUserIdAndCheckDateBetween(
                userId, today.withDayOfMonth(1).minusMonths(1), today.withDayOfMonth(1).minusDays(1)
            )
        } returns emptyList()

        every { streakRepository.findByUserId(userId) } returns Streak(userId = userId, currentStreak = 5, longestStreak = 10)

        val result = statisticsService.getSummaryStats(userId)

        assertThat(result.thisWeekDrinking).isEqualTo(1)
        assertThat(result.lastWeekDrinking).isEqualTo(3)
        assertThat(result.weekChange).isEqualTo(-2)
        assertThat(result.currentStreak).isEqualTo(5)
        assertThat(result.longestStreak).isEqualTo(10)
    }
}

package com.drinky.service

import com.drinky.domain.entity.CheckIn
import com.drinky.domain.entity.Streak
import com.drinky.repository.CheckInRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class CheckInServiceTest {

    private lateinit var checkInRepository: CheckInRepository
    private lateinit var streakService: StreakService
    private lateinit var checkInService: CheckInService

    @BeforeEach
    fun setUp() {
        checkInRepository = mockk()
        streakService = mockk()
        checkInService = CheckInService(checkInRepository, streakService)

        // Default streak service mock
        every { streakService.updateStreak(any(), any()) } returns Streak(userId = UUID.randomUUID())
    }

    @Test
    fun `first check-in creates new record`() {
        val userId = UUID.randomUUID()
        val checkInSlot = slot<CheckIn>()

        every { checkInRepository.findByUserIdAndCheckDate(userId, LocalDate.now()) } returns null
        every { checkInRepository.save(capture(checkInSlot)) } answers { checkInSlot.captured }

        val result = checkInService.checkIn(userId, true)

        assertThat(result.userId).isEqualTo(userId)
        assertThat(result.checkDate).isEqualTo(LocalDate.now())
        assertThat(result.isSober).isTrue()
        verify(exactly = 1) { checkInRepository.save(any()) }
    }

    @Test
    fun `second check-in updates existing record`() {
        val userId = UUID.randomUUID()
        val existingCheckIn = CheckIn(
            id = UUID.randomUUID(),
            userId = userId,
            checkDate = LocalDate.now(),
            isSober = true
        )

        every { checkInRepository.findByUserIdAndCheckDate(userId, LocalDate.now()) } returns existingCheckIn
        every { checkInRepository.save(any()) } answers { firstArg() }

        val result = checkInService.checkIn(userId, false)

        assertThat(result.isSober).isFalse()
        verify(exactly = 1) { checkInRepository.save(existingCheckIn) }
    }

    @Test
    fun `getTodayCheckIn returns check-in for today`() {
        val userId = UUID.randomUUID()
        val todayCheckIn = CheckIn(
            id = UUID.randomUUID(),
            userId = userId,
            checkDate = LocalDate.now(),
            isSober = true
        )

        every { checkInRepository.findByUserIdAndCheckDate(userId, LocalDate.now()) } returns todayCheckIn

        val result = checkInService.getTodayCheckIn(userId)

        assertThat(result).isNotNull
        assertThat(result!!.isSober).isTrue()
    }

    @Test
    fun `getTodayCheckIn returns null when no check-in`() {
        val userId = UUID.randomUUID()

        every { checkInRepository.findByUserIdAndCheckDate(userId, LocalDate.now()) } returns null

        val result = checkInService.getTodayCheckIn(userId)

        assertThat(result).isNull()
    }
}

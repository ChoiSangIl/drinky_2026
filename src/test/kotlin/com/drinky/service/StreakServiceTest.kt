package com.drinky.service

import com.drinky.domain.entity.CheckIn
import com.drinky.domain.entity.Streak
import com.drinky.repository.CheckInRepository
import com.drinky.repository.StreakRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class StreakServiceTest {

    private lateinit var streakRepository: StreakRepository
    private lateinit var checkInRepository: CheckInRepository
    private lateinit var streakService: StreakService

    @BeforeEach
    fun setUp() {
        streakRepository = mockk()
        checkInRepository = mockk()
        streakService = StreakService(streakRepository, checkInRepository)
    }

    @Test
    fun `first sober check-in starts streak at 1`() {
        val userId = UUID.randomUUID()
        val streakSlot = slot<Streak>()

        every { streakRepository.findByUserId(userId) } returns null
        every { streakRepository.save(capture(streakSlot)) } answers { streakSlot.captured }

        val checkIn = CheckIn(userId = userId, checkDate = LocalDate.now(), isSober = true)
        val result = streakService.updateStreak(userId, checkIn)

        assertThat(result.currentStreak).isEqualTo(1)
        assertThat(result.longestStreak).isEqualTo(1)
        assertThat(result.streakStartDate).isEqualTo(LocalDate.now())
    }

    @Test
    fun `consecutive sober days increase streak`() {
        val userId = UUID.randomUUID()
        val yesterday = LocalDate.now().minusDays(1)
        val existingStreak = Streak(
            id = UUID.randomUUID(),
            userId = userId,
            currentStreak = 3,
            longestStreak = 3,
            streakStartDate = yesterday.minusDays(2),
            lastCheckDate = yesterday
        )

        every { streakRepository.findByUserId(userId) } returns existingStreak
        every { streakRepository.save(any()) } answers { firstArg() }

        val checkIn = CheckIn(userId = userId, checkDate = LocalDate.now(), isSober = true)
        val result = streakService.updateStreak(userId, checkIn)

        assertThat(result.currentStreak).isEqualTo(4)
        assertThat(result.longestStreak).isEqualTo(4)
    }

    @Test
    fun `drinking resets current streak to 0`() {
        val userId = UUID.randomUUID()
        val existingStreak = Streak(
            id = UUID.randomUUID(),
            userId = userId,
            currentStreak = 5,
            longestStreak = 5,
            streakStartDate = LocalDate.now().minusDays(4),
            lastCheckDate = LocalDate.now().minusDays(1)
        )

        every { streakRepository.findByUserId(userId) } returns existingStreak
        every { streakRepository.save(any()) } answers { firstArg() }

        val checkIn = CheckIn(userId = userId, checkDate = LocalDate.now(), isSober = false)
        val result = streakService.updateStreak(userId, checkIn)

        assertThat(result.currentStreak).isEqualTo(0)
        assertThat(result.longestStreak).isEqualTo(5) // longest preserved
        assertThat(result.streakStartDate).isNull()
    }

    @Test
    fun `same day check-in does not increase streak`() {
        val userId = UUID.randomUUID()
        val today = LocalDate.now()
        val existingStreak = Streak(
            id = UUID.randomUUID(),
            userId = userId,
            currentStreak = 3,
            longestStreak = 3,
            streakStartDate = today.minusDays(2),
            lastCheckDate = today // already checked in today
        )

        every { streakRepository.findByUserId(userId) } returns existingStreak
        every { streakRepository.save(any()) } answers { firstArg() }

        val checkIn = CheckIn(userId = userId, checkDate = today, isSober = true)
        val result = streakService.updateStreak(userId, checkIn)

        assertThat(result.currentStreak).isEqualTo(3) // unchanged
    }

    @Test
    fun `longest streak updates when current exceeds it`() {
        val userId = UUID.randomUUID()
        val yesterday = LocalDate.now().minusDays(1)
        val existingStreak = Streak(
            id = UUID.randomUUID(),
            userId = userId,
            currentStreak = 5,
            longestStreak = 5,
            streakStartDate = yesterday.minusDays(4),
            lastCheckDate = yesterday
        )

        every { streakRepository.findByUserId(userId) } returns existingStreak
        every { streakRepository.save(any()) } answers { firstArg() }

        val checkIn = CheckIn(userId = userId, checkDate = LocalDate.now(), isSober = true)
        val result = streakService.updateStreak(userId, checkIn)

        assertThat(result.currentStreak).isEqualTo(6)
        assertThat(result.longestStreak).isEqualTo(6)
    }

    @Test
    fun `getOrCreateStreak returns existing streak`() {
        val userId = UUID.randomUUID()
        val existingStreak = Streak(id = UUID.randomUUID(), userId = userId, currentStreak = 3)

        every { streakRepository.findByUserId(userId) } returns existingStreak

        val result = streakService.getOrCreateStreak(userId)

        assertThat(result.currentStreak).isEqualTo(3)
        verify(exactly = 0) { streakRepository.save(any()) }
    }

    @Test
    fun `getOrCreateStreak creates new streak if not exists`() {
        val userId = UUID.randomUUID()
        val streakSlot = slot<Streak>()

        every { streakRepository.findByUserId(userId) } returns null
        every { streakRepository.save(capture(streakSlot)) } answers { streakSlot.captured }

        val result = streakService.getOrCreateStreak(userId)

        assertThat(result.userId).isEqualTo(userId)
        assertThat(result.currentStreak).isEqualTo(0)
        verify(exactly = 1) { streakRepository.save(any()) }
    }

    @Test
    fun `isStreakJustBroken returns true when streak was reset`() {
        val userId = UUID.randomUUID()
        val brokenStreak = Streak(
            id = UUID.randomUUID(),
            userId = userId,
            currentStreak = 0,
            longestStreak = 5
        )

        every { streakRepository.findByUserId(userId) } returns brokenStreak

        val result = streakService.isStreakJustBroken(userId)

        assertThat(result).isTrue()
    }

    @Test
    fun `recalculateStreak computes correct streak from all check-ins`() {
        val userId = UUID.randomUUID()
        val existingStreak = Streak(id = UUID.randomUUID(), userId = userId)
        val checkIns = listOf(
            CheckIn(userId = userId, checkDate = LocalDate.of(2026, 2, 14), isSober = true),
            CheckIn(userId = userId, checkDate = LocalDate.of(2026, 2, 15), isSober = true),
            CheckIn(userId = userId, checkDate = LocalDate.of(2026, 2, 16), isSober = false),
            CheckIn(userId = userId, checkDate = LocalDate.of(2026, 2, 17), isSober = true),
            CheckIn(userId = userId, checkDate = LocalDate.of(2026, 2, 18), isSober = true),
            CheckIn(userId = userId, checkDate = LocalDate.of(2026, 2, 19), isSober = true)
        )

        every { checkInRepository.findByUserIdOrderByCheckDateAsc(userId) } returns checkIns
        every { streakRepository.findByUserId(userId) } returns existingStreak
        every { streakRepository.save(any()) } answers { firstArg() }

        val result = streakService.recalculateStreak(userId)

        assertThat(result.currentStreak).isEqualTo(3) // 17, 18, 19
        assertThat(result.longestStreak).isEqualTo(3) // max(2, 3) = 3
        assertThat(result.streakStartDate).isEqualTo(LocalDate.of(2026, 2, 17))
        assertThat(result.lastCheckDate).isEqualTo(LocalDate.of(2026, 2, 19))
    }

    @Test
    fun `recalculateStreak with no check-ins`() {
        val userId = UUID.randomUUID()
        val existingStreak = Streak(id = UUID.randomUUID(), userId = userId, currentStreak = 5, longestStreak = 5)

        every { checkInRepository.findByUserIdOrderByCheckDateAsc(userId) } returns emptyList()
        every { streakRepository.findByUserId(userId) } returns existingStreak
        every { streakRepository.save(any()) } answers { firstArg() }

        val result = streakService.recalculateStreak(userId)

        assertThat(result.currentStreak).isEqualTo(0)
        assertThat(result.lastCheckDate).isNull()
    }

    @Test
    fun `recalculateStreak preserves longest streak from past`() {
        val userId = UUID.randomUUID()
        val existingStreak = Streak(id = UUID.randomUUID(), userId = userId, longestStreak = 10)
        val checkIns = listOf(
            CheckIn(userId = userId, checkDate = LocalDate.of(2026, 2, 18), isSober = true),
            CheckIn(userId = userId, checkDate = LocalDate.of(2026, 2, 19), isSober = true)
        )

        every { checkInRepository.findByUserIdOrderByCheckDateAsc(userId) } returns checkIns
        every { streakRepository.findByUserId(userId) } returns existingStreak
        every { streakRepository.save(any()) } answers { firstArg() }

        val result = streakService.recalculateStreak(userId)

        assertThat(result.currentStreak).isEqualTo(2)
        assertThat(result.longestStreak).isEqualTo(10) // preserves old longest
    }

    @Test
    fun `isStreakJustBroken returns false when streak is active`() {
        val userId = UUID.randomUUID()
        val activeStreak = Streak(
            id = UUID.randomUUID(),
            userId = userId,
            currentStreak = 3,
            longestStreak = 5
        )

        every { streakRepository.findByUserId(userId) } returns activeStreak

        val result = streakService.isStreakJustBroken(userId)

        assertThat(result).isFalse()
    }
}

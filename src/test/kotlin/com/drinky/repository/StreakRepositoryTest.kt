package com.drinky.repository

import com.drinky.domain.entity.Streak
import com.drinky.domain.entity.User
import com.drinky.domain.enums.AuthProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.UUID

@DataJpaTest
@ActiveProfiles("test")
class StreakRepositoryTest @Autowired constructor(
    private val streakRepository: StreakRepository,
    private val userRepository: UserRepository
) {

    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        testUser = userRepository.save(
            User(
                email = "streak-test-${UUID.randomUUID()}@test.com",
                nickname = "테스트유저",
                provider = AuthProvider.GOOGLE,
                providerId = "google-${UUID.randomUUID()}"
            )
        )
    }

    @Test
    fun `save and find streak by user id`() {
        val streak = streakRepository.save(
            Streak(
                userId = testUser.id!!,
                currentStreak = 5,
                longestStreak = 10,
                streakStartDate = LocalDate.now().minusDays(4),
                lastCheckDate = LocalDate.now()
            )
        )

        val found = streakRepository.findByUserId(testUser.id!!)

        assertThat(found).isNotNull
        assertThat(found!!.id).isEqualTo(streak.id)
        assertThat(found.currentStreak).isEqualTo(5)
        assertThat(found.longestStreak).isEqualTo(10)
    }

    @Test
    fun `returns null when streak not found`() {
        val result = streakRepository.findByUserId(testUser.id!!)

        assertThat(result).isNull()
    }

    @Test
    fun `update existing streak`() {
        val streak = streakRepository.save(
            Streak(
                userId = testUser.id!!,
                currentStreak = 3
            )
        )

        streak.currentStreak = 4
        streak.longestStreak = 4
        streakRepository.save(streak)

        val updated = streakRepository.findByUserId(testUser.id!!)

        assertThat(updated!!.currentStreak).isEqualTo(4)
        assertThat(updated.longestStreak).isEqualTo(4)
    }
}

package com.drinky.repository

import com.drinky.domain.entity.CheckIn
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
class CheckInRepositoryTest @Autowired constructor(
    private val checkInRepository: CheckInRepository,
    private val userRepository: UserRepository
) {

    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        testUser = userRepository.save(
            User(
                email = "test-${UUID.randomUUID()}@test.com",
                nickname = "테스트유저",
                provider = AuthProvider.GOOGLE,
                providerId = "google-${UUID.randomUUID()}"
            )
        )
    }

    @Test
    fun `save and find check-in by user and date`() {
        val today = LocalDate.now()
        val checkIn = checkInRepository.save(
            CheckIn(
                userId = testUser.id!!,
                checkDate = today,
                isSober = true
            )
        )

        val found = checkInRepository.findByUserIdAndCheckDate(testUser.id!!, today)

        assertThat(found).isNotNull
        assertThat(found!!.id).isEqualTo(checkIn.id)
        assertThat(found.isSober).isTrue()
    }

    @Test
    fun `find check-ins between dates`() {
        val today = LocalDate.now()
        checkInRepository.save(CheckIn(userId = testUser.id!!, checkDate = today, isSober = true))
        checkInRepository.save(CheckIn(userId = testUser.id!!, checkDate = today.minusDays(1), isSober = false))
        checkInRepository.save(CheckIn(userId = testUser.id!!, checkDate = today.minusDays(2), isSober = true))

        val results = checkInRepository.findByUserIdAndCheckDateBetween(
            testUser.id!!,
            today.minusDays(2),
            today
        )

        assertThat(results).hasSize(3)
    }

    @Test
    fun `returns null when check-in not found`() {
        val result = checkInRepository.findByUserIdAndCheckDate(testUser.id!!, LocalDate.now())

        assertThat(result).isNull()
    }
}

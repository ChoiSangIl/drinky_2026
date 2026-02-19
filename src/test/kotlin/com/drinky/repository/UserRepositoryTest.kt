package com.drinky.repository

import com.drinky.domain.entity.User
import com.drinky.domain.enums.AuthProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository
) {

    @Test
    fun `should save and find user by email`() {
        val user = User(
            email = "test@example.com",
            nickname = "테스트",
            provider = AuthProvider.GOOGLE,
            providerId = "google-123"
        )
        userRepository.save(user)

        val found = userRepository.findByEmail("test@example.com")
        assertThat(found).isNotNull
        assertThat(found!!.nickname).isEqualTo("테스트")
        assertThat(found.provider).isEqualTo(AuthProvider.GOOGLE)
    }

    @Test
    fun `should find user by provider and provider id`() {
        val user = User(
            email = "kakao@example.com",
            nickname = "카카오유저",
            provider = AuthProvider.KAKAO,
            providerId = "kakao-456"
        )
        userRepository.save(user)

        val found = userRepository.findByProviderAndProviderId(AuthProvider.KAKAO, "kakao-456")
        assertThat(found).isNotNull
        assertThat(found!!.email).isEqualTo("kakao@example.com")
    }

    @Test
    fun `should return null when user not found`() {
        val found = userRepository.findByEmail("nonexistent@example.com")
        assertThat(found).isNull()
    }

    @Test
    fun `should return null when provider id not found`() {
        val found = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, "not-exist")
        assertThat(found).isNull()
    }
}

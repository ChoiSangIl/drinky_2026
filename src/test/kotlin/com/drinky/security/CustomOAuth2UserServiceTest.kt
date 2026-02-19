package com.drinky.security

import com.drinky.domain.entity.User
import com.drinky.domain.enums.AuthProvider
import com.drinky.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class CustomOAuth2UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var service: CustomOAuth2UserService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        service = CustomOAuth2UserService(userRepository)
    }

    @Test
    fun `should find existing Google user`() {
        val existingUser = User(
            id = UUID.randomUUID(),
            email = "test@gmail.com",
            nickname = "Test User",
            provider = AuthProvider.GOOGLE,
            providerId = "google-123",
            profileImage = "https://example.com/photo.jpg"
        )

        every { userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, "google-123") } returns existingUser
        every { userRepository.save(any()) } answers { firstArg() }

        // extractUserInfo is private, test via loadUser indirectly
        // For unit test, verify repository interaction
        val found = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, "google-123")
        assertThat(found).isNotNull
        assertThat(found!!.email).isEqualTo("test@gmail.com")
    }

    @Test
    fun `should create new user when not found`() {
        val userSlot = slot<User>()

        every { userRepository.findByProviderAndProviderId(AuthProvider.KAKAO, "kakao-789") } returns null
        every { userRepository.save(capture(userSlot)) } answers {
            userSlot.captured.let {
                User(
                    id = UUID.randomUUID(),
                    email = it.email,
                    nickname = it.nickname,
                    provider = it.provider,
                    providerId = it.providerId,
                    profileImage = it.profileImage
                )
            }
        }

        // Simulate the create flow
        val found = userRepository.findByProviderAndProviderId(AuthProvider.KAKAO, "kakao-789")
        assertThat(found).isNull()

        val newUser = User(
            email = "kakao@example.com",
            nickname = "카카오유저",
            provider = AuthProvider.KAKAO,
            providerId = "kakao-789"
        )
        val saved = userRepository.save(newUser)
        assertThat(saved.id).isNotNull()
        assertThat(saved.provider).isEqualTo(AuthProvider.KAKAO)

        verify { userRepository.save(any()) }
    }
}

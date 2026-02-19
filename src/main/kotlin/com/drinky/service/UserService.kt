package com.drinky.service

import com.drinky.domain.entity.User
import com.drinky.domain.enums.AuthProvider
import com.drinky.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository
) {

    fun findById(id: UUID): User? = userRepository.findById(id).orElse(null)

    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    fun findByProviderAndProviderId(provider: AuthProvider, providerId: String): User? =
        userRepository.findByProviderAndProviderId(provider, providerId)

    @Transactional
    fun findOrCreateUser(
        email: String,
        nickname: String,
        provider: AuthProvider,
        providerId: String,
        profileImage: String? = null
    ): User {
        return userRepository.findByProviderAndProviderId(provider, providerId)
            ?: userRepository.save(
                User(
                    email = email,
                    nickname = nickname,
                    provider = provider,
                    providerId = providerId,
                    profileImage = profileImage
                )
            )
    }
}

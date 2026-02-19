package com.drinky.security

import com.drinky.domain.entity.User
import com.drinky.domain.enums.AuthProvider
import com.drinky.repository.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val registrationId = userRequest.clientRegistration.registrationId
        val provider = AuthProvider.valueOf(registrationId.uppercase())

        val userInfo = extractUserInfo(provider, oAuth2User.attributes)
        val user = userRepository.findByProviderAndProviderId(provider, userInfo.providerId)
            ?: createUser(provider, userInfo)

        updateUserIfNeeded(user, userInfo)

        return UserPrincipal.create(user, oAuth2User.attributes)
    }

    private fun extractUserInfo(provider: AuthProvider, attributes: Map<String, Any>): OAuth2UserInfo {
        return when (provider) {
            AuthProvider.GOOGLE -> OAuth2UserInfo(
                providerId = attributes["sub"] as String,
                email = attributes["email"] as String,
                nickname = attributes["name"] as String,
                profileImage = attributes["picture"] as? String
            )
            AuthProvider.KAKAO -> {
                val kakaoAccount = attributes["kakao_account"] as? Map<*, *> ?: emptyMap<String, Any>()
                val properties = attributes["properties"] as? Map<*, *> ?: emptyMap<String, Any>()
                OAuth2UserInfo(
                    providerId = attributes["id"].toString(),
                    email = kakaoAccount["email"] as? String ?: "",
                    nickname = properties["nickname"] as? String ?: "사용자",
                    profileImage = properties["profile_image"] as? String
                )
            }
        }
    }

    private fun createUser(provider: AuthProvider, userInfo: OAuth2UserInfo): User {
        val user = User(
            email = userInfo.email,
            nickname = userInfo.nickname,
            provider = provider,
            providerId = userInfo.providerId,
            profileImage = userInfo.profileImage
        )
        return userRepository.save(user)
    }

    private fun updateUserIfNeeded(user: User, userInfo: OAuth2UserInfo) {
        var updated = false
        if (user.nickname != userInfo.nickname) {
            user.nickname = userInfo.nickname
            updated = true
        }
        if (user.profileImage != userInfo.profileImage) {
            user.profileImage = userInfo.profileImage
            updated = true
        }
        if (updated) {
            user.updatedAt = java.time.LocalDateTime.now()
            userRepository.save(user)
        }
    }

    data class OAuth2UserInfo(
        val providerId: String,
        val email: String,
        val nickname: String,
        val profileImage: String?
    )
}

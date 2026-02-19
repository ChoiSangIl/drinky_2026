package com.drinky.security

import com.drinky.domain.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import java.util.UUID

class UserPrincipal(
    val id: UUID,
    val email: String,
    val nickname: String,
    val profileImage: String?,
    private val authorities: Collection<GrantedAuthority>,
    private val attributes: MutableMap<String, Any>
) : OAuth2User {

    override fun getName(): String = id.toString()

    override fun getAttributes(): MutableMap<String, Any> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    companion object {
        fun create(user: User, attributes: Map<String, Any>): UserPrincipal {
            return UserPrincipal(
                id = user.id!!,
                email = user.email,
                nickname = user.nickname,
                profileImage = user.profileImage,
                authorities = listOf(SimpleGrantedAuthority("ROLE_USER")),
                attributes = attributes.toMutableMap()
            )
        }
    }
}

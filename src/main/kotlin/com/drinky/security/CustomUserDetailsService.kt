package com.drinky.security

import com.drinky.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = try {
            userRepository.findById(UUID.fromString(username)).orElse(null)
        } catch (e: IllegalArgumentException) {
            userRepository.findByEmail(username)
        } ?: throw UsernameNotFoundException("User not found: $username")

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.id.toString())
            .password("") // OAuth2 사용자는 패스워드 없음
            .authorities(SimpleGrantedAuthority("ROLE_USER"))
            .build()
    }
}

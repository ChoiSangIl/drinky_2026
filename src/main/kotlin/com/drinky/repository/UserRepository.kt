package com.drinky.repository

import com.drinky.domain.entity.User
import com.drinky.domain.enums.AuthProvider
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findByProviderAndProviderId(provider: AuthProvider, providerId: String): User?
}

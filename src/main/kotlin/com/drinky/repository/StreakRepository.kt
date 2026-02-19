package com.drinky.repository

import com.drinky.domain.entity.Streak
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface StreakRepository : JpaRepository<Streak, UUID> {
    fun findByUserId(userId: UUID): Streak?
}

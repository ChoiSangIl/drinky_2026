package com.drinky.repository

import com.drinky.domain.entity.CheckIn
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface CheckInRepository : JpaRepository<CheckIn, UUID> {
    fun findByUserIdAndCheckDate(userId: UUID, checkDate: LocalDate): CheckIn?
    fun findByUserIdOrderByCheckDateDesc(userId: UUID, pageable: Pageable): List<CheckIn>
    fun findByUserIdAndCheckDateBetween(userId: UUID, startDate: LocalDate, endDate: LocalDate): List<CheckIn>
}

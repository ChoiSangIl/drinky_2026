package com.drinky.domain.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "streaks")
class Streak(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "user_id", nullable = false, unique = true)
    val userId: UUID,

    @Column(name = "current_streak", nullable = false)
    var currentStreak: Int = 0,

    @Column(name = "longest_streak", nullable = false)
    var longestStreak: Int = 0,

    @Column(name = "streak_start_date")
    var streakStartDate: LocalDate? = null,

    @Column(name = "last_check_date")
    var lastCheckDate: LocalDate? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

package com.drinky.domain.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "check_ins")
class CheckIn(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "check_date", nullable = false)
    val checkDate: LocalDate,

    @Column(name = "is_sober", nullable = false)
    var isSober: Boolean,

    @Column(length = 200)
    var note: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

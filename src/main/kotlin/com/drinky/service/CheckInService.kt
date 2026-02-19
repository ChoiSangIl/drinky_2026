package com.drinky.service

import com.drinky.domain.entity.CheckIn
import com.drinky.repository.CheckInRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CheckInService(
    private val checkInRepository: CheckInRepository,
    private val streakService: StreakService
) {

    fun getTodayCheckIn(userId: UUID): CheckIn? {
        return checkInRepository.findByUserIdAndCheckDate(userId, LocalDate.now())
    }

    fun getCheckIn(userId: UUID, date: LocalDate): CheckIn? {
        return checkInRepository.findByUserIdAndCheckDate(userId, date)
    }

    @Transactional
    fun checkIn(userId: UUID, isSober: Boolean, note: String? = null): CheckIn {
        val today = LocalDate.now()
        val existing = checkInRepository.findByUserIdAndCheckDate(userId, today)

        val checkIn = if (existing != null) {
            existing.isSober = isSober
            existing.note = note
            checkInRepository.save(existing)
        } else {
            checkInRepository.save(
                CheckIn(
                    userId = userId,
                    checkDate = today,
                    isSober = isSober,
                    note = note
                )
            )
        }

        // 스트릭 업데이트
        streakService.updateStreak(userId, checkIn)

        return checkIn
    }

    fun getRecentCheckIns(userId: UUID, days: Int = 7): List<CheckIn> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(days.toLong() - 1)
        return checkInRepository.findByUserIdAndCheckDateBetween(userId, startDate, endDate)
    }
}

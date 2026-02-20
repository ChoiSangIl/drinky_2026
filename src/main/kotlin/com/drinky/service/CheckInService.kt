package com.drinky.service

import com.drinky.domain.entity.CheckIn
import com.drinky.repository.CheckInRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CheckInService(
    private val checkInRepository: CheckInRepository,
    private val streakService: StreakService
) {

    private val log = LoggerFactory.getLogger(CheckInService::class.java)

    companion object {
        const val MAX_EDIT_DAYS = 7L
    }

    fun isEditable(checkDate: LocalDate): Boolean {
        val today = LocalDate.now()
        val daysAgo = ChronoUnit.DAYS.between(checkDate, today)
        return daysAgo in 0..MAX_EDIT_DAYS && !checkDate.isAfter(today)
    }

    @Transactional
    fun checkInForDate(userId: UUID, date: LocalDate, isSober: Boolean): CheckIn {
        val existing = checkInRepository.findByUserIdAndCheckDate(userId, date)

        val checkIn = if (existing != null) {
            val oldValue = existing.isSober
            existing.isSober = isSober
            val saved = checkInRepository.save(existing)
            log.info("체크인 수정: userId={}, date={}, oldValue={}, newValue={}", userId, date, oldValue, isSober)
            saved
        } else {
            val saved = checkInRepository.save(
                CheckIn(
                    userId = userId,
                    checkDate = date,
                    isSober = isSober
                )
            )
            log.info("체크인 추가: userId={}, date={}, value={}", userId, date, isSober)
            saved
        }

        streakService.recalculateStreak(userId)
        return checkIn
    }

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

    fun getMonthlyCheckIns(userId: UUID, year: Int, month: Int): Map<LocalDate, CheckIn> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())
        return checkInRepository.findByUserIdAndCheckDateBetween(userId, startDate, endDate)
            .associateBy { it.checkDate }
    }

    fun getRecentCheckIns(userId: UUID, days: Int = 7): List<CheckIn> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(days.toLong() - 1)
        return checkInRepository.findByUserIdAndCheckDateBetween(userId, startDate, endDate)
    }
}

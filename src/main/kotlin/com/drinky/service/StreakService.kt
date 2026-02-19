package com.drinky.service

import com.drinky.domain.entity.CheckIn
import com.drinky.domain.entity.Streak
import com.drinky.repository.StreakRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional(readOnly = true)
class StreakService(
    private val streakRepository: StreakRepository
) {

    fun getOrCreateStreak(userId: UUID): Streak {
        return streakRepository.findByUserId(userId)
            ?: streakRepository.save(Streak(userId = userId))
    }

    @Transactional
    fun updateStreak(userId: UUID, checkIn: CheckIn): Streak {
        val streak = getOrCreateStreak(userId)
        val today = checkIn.checkDate

        if (checkIn.isSober) {
            // 연속 체크인 확인 (어제 또는 오늘 체크했으면 연속)
            val isConsecutive = streak.lastCheckDate?.plusDays(1) == today
                    || streak.lastCheckDate == today

            if (isConsecutive || streak.currentStreak == 0) {
                // 오늘 처음 체크하는 경우만 증가
                if (streak.lastCheckDate != today) {
                    streak.currentStreak++
                }
                if (streak.streakStartDate == null) {
                    streak.streakStartDate = today
                }
            } else {
                // 연속 끊김 후 새로 시작
                streak.currentStreak = 1
                streak.streakStartDate = today
            }

            // 최장 스트릭 갱신
            if (streak.currentStreak > streak.longestStreak) {
                streak.longestStreak = streak.currentStreak
            }
        } else {
            // 스트릭 리셋
            streak.currentStreak = 0
            streak.streakStartDate = null
        }

        streak.lastCheckDate = today
        streak.updatedAt = LocalDateTime.now()
        return streakRepository.save(streak)
    }

    fun isStreakJustBroken(userId: UUID): Boolean {
        val streak = streakRepository.findByUserId(userId) ?: return false
        return streak.currentStreak == 0 && streak.longestStreak > 0
    }
}

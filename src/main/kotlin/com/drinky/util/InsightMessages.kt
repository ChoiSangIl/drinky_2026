package com.drinky.util

import com.drinky.domain.dto.SummaryStatDto

object InsightMessages {

    fun generate(stats: SummaryStatDto): String {
        return when {
            stats.currentStreak >= 30 -> "30일 연속 절주! 정말 대단해요! \uD83C\uDF89"
            stats.currentStreak >= 14 -> "2주 연속 절주 중! 멋진 습관이 만들어지고 있어요! \uD83D\uDCAA"
            stats.currentStreak >= 7 -> "일주일 연속 절주! 이 기세 그대로! \uD83D\uDD25"
            stats.weekChange < 0 -> "지난 주보다 ${-stats.weekChange}일 줄었어요! \uD83C\uDF89"
            stats.weekChange == 0 && stats.thisWeekDrinking == 0 -> "이번 주 완벽한 절주! 최고예요! \u2B50"
            stats.weekChange == 0 -> "꾸준히 잘 하고 있어요! \uD83D\uDCAA"
            stats.weekChange > 0 -> "이번 주는 조금 힘들었네요. 다음 주 화이팅! \uD83D\uDE4F"
            else -> "오늘부터 새로운 시작! 화이팅! \uD83D\uDCAA"
        }
    }
}

package com.drinky.domain.dto

data class SummaryStatDto(
    val thisWeekDrinking: Int,
    val lastWeekDrinking: Int,
    val weekChange: Int,
    val thisMonthDrinking: Int,
    val lastMonthDrinking: Int,
    val monthChange: Int,
    val currentStreak: Int,
    val longestStreak: Int
) {
    val weekTrend: String get() = when {
        weekChange < 0 -> "↓ ${-weekChange}일 감소"
        weekChange > 0 -> "↑ ${weekChange}일 증가"
        else -> "- 변화 없음"
    }

    val monthTrend: String get() = when {
        monthChange < 0 -> "↓ ${-monthChange}일 감소"
        monthChange > 0 -> "↑ ${monthChange}일 증가"
        else -> "- 변화 없음"
    }
}

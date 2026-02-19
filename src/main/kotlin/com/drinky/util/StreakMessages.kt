package com.drinky.util

object StreakMessages {

    fun getMessage(streak: Int, justBroken: Boolean = false): String {
        if (justBroken) {
            return brokenMessages.random()
        }

        return when (streak) {
            0 -> zeroMessages.random()
            1, 2 -> startMessages.random()
            in 3..6 -> progressMessages.random()
            in 7..13 -> weekMessages.random()
            in 14..29 -> twoWeekMessages.random()
            else -> monthMessages.random()
        }
    }

    private val zeroMessages = listOf(
        "오늘부터 시작해볼까요?",
        "새로운 시작, 응원할게요!",
        "첫 걸음이 가장 중요해요"
    )

    private val startMessages = listOf(
        "좋은 시작이에요!",
        "잘 하고 있어요!",
        "시작이 반이에요!"
    )

    private val progressMessages = listOf(
        "잘 하고 있어요! 계속 가봐요!",
        "꾸준함이 대단해요!",
        "멋진 습관이 만들어지고 있어요!"
    )

    private val weekMessages = listOf(
        "대단해요! 일주일 넘게 성공!",
        "일주일 달성! 정말 잘하고 있어요!",
        "벌써 일주일! 놀라워요!"
    )

    private val twoWeekMessages = listOf(
        "2주 연속! 놀라워요!",
        "보름이 넘었어요! 대단해요!",
        "2주 넘게 지속 중! 최고예요!"
    )

    private val monthMessages = listOf(
        "한 달 이상! 정말 대단해요!",
        "놀라운 의지력이에요!",
        "당신은 진정한 절주 마스터!"
    )

    private val brokenMessages = listOf(
        "괜찮아요, 다시 시작하면 돼요",
        "실수해도 괜찮아요. 내일 다시!",
        "포기하지 않는 게 중요해요"
    )
}

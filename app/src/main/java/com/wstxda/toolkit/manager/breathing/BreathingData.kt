package com.wstxda.toolkit.manager.breathing

data class BreathingData(
    val phase: BreathingPhase = BreathingPhase.IDLE,
    val progress: Float = 0f,
    val secondsRemaining: Int = 0
)
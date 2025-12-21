package com.wstxda.toolkit.manager.breathing

import android.content.Context
import com.wstxda.toolkit.ui.utils.Haptics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.math.ceil

class BreathingManager(context: Context) {

    companion object {
        private const val DURATION_PREPARING = 1000L
        private const val DURATION_INHALE = 4000L
        private const val DURATION_HOLD_FULL = 2000L
        private const val DURATION_EXHALE = 4000L
        private const val DURATION_HOLD_EMPTY = 1000L
        private const val FRAME_RATE_MS = 50L
        private const val INHALE_TICK_INTERVAL = 120L
    }

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val haptics = Haptics(context.applicationContext)
    private val _breathingState = MutableStateFlow(BreathingData())
    val breathingState = _breathingState.asStateFlow()
    private var animationJob: Job? = null

    fun toggle() {
        if (animationJob?.isActive == true) stop() else start()
    }

    private fun start() {
        animationJob?.cancel()
        animationJob = managerScope.launch {
            runPhase(BreathingPhase.PREPARING, DURATION_PREPARING, 1f, 1f)
            while (isActive) {
                runCycle()
            }
        }
    }

    private suspend fun runCycle() {
        runPhase(BreathingPhase.INHALE, DURATION_INHALE, 0f, 1f, useHaptics = true)
        runPhase(BreathingPhase.HOLD_FULL, DURATION_HOLD_FULL, 1f, 1f)
        runPhase(BreathingPhase.EXHALE, DURATION_EXHALE, 1f, 0f)
        runPhase(BreathingPhase.HOLD_EMPTY, DURATION_HOLD_EMPTY, 0f, 0f)
    }

    private suspend fun runPhase(
        phase: BreathingPhase,
        duration: Long,
        startVal: Float,
        endVal: Float,
        useHaptics: Boolean = false
    ) {
        val startTime = System.currentTimeMillis()
        var elapsedTime = 0L
        var nextHapticTrigger = 0L

        while (elapsedTime < duration && coroutineContext.isActive) {
            elapsedTime = System.currentTimeMillis() - startTime

            if (useHaptics && elapsedTime >= nextHapticTrigger) {
                haptics.tick()
                nextHapticTrigger += INHALE_TICK_INTERVAL
            }

            val fraction = (elapsedTime.toFloat() / duration).coerceIn(0f, 1f)
            val currentProgress = startVal + (endVal - startVal) * fraction
            val secondsLeft = ceil((duration - elapsedTime) / 1000.0).toInt().coerceAtLeast(1)

            _breathingState.value = BreathingData(phase, currentProgress, secondsLeft)
            delay(FRAME_RATE_MS)
        }
    }

    fun stop() {
        animationJob?.cancel()
        animationJob = null
        _breathingState.value = BreathingData(BreathingPhase.IDLE, 0f, 0)
    }

    fun cleanup() {
        stop()
        managerScope.cancel()
    }
}
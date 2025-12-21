package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R
import com.wstxda.toolkit.manager.breathing.BreathingPhase

class BreathingLabelProvider(private val context: Context) {

    fun getLabel(phase: BreathingPhase): CharSequence {
        return when (phase) {
            BreathingPhase.PREPARING, BreathingPhase.IDLE -> context.getString(R.string.breathing_tile)
            BreathingPhase.INHALE -> context.getString(R.string.breathing_tile_inhale)
            BreathingPhase.HOLD_FULL -> context.getString(R.string.breathing_tile_pause)
            BreathingPhase.EXHALE -> context.getString(R.string.breathing_tile_exhale)
            BreathingPhase.HOLD_EMPTY -> context.getString(R.string.breathing_tile_relax)
        }
    }

    fun getSubtitle(phase: BreathingPhase): CharSequence {
        return if (phase == BreathingPhase.IDLE) {
            context.getString(R.string.tile_start)
        } else {
            context.getString(R.string.breathing_tile_prepare)
        }
    }
}
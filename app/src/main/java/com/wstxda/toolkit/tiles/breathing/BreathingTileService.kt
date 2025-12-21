package com.wstxda.toolkit.tiles.breathing

import android.service.quicksettings.Tile
import com.wstxda.toolkit.base.BaseTileService
import com.wstxda.toolkit.manager.breathing.BreathingManager
import com.wstxda.toolkit.manager.breathing.BreathingPhase
import com.wstxda.toolkit.ui.icon.BreathingIconProvider
import com.wstxda.toolkit.ui.label.BreathingLabelProvider
import kotlinx.coroutines.flow.Flow

class BreathingTileService : BaseTileService() {

    private val breathingManager by lazy { BreathingManager(applicationContext) }
    private val breathingLabelProvider by lazy { BreathingLabelProvider(applicationContext) }
    private val breathingIconProvider by lazy { BreathingIconProvider(applicationContext) }

    override fun onStopListening() {
        super.onStopListening()
        breathingManager.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        breathingManager.cleanup()
    }

    override fun onClick() {
        breathingManager.toggle()
    }

    override fun flowsToCollect(): List<Flow<*>> {
        return listOf(breathingManager.breathingState)
    }

    override fun updateTile() {
        val state = breathingManager.breathingState.value
        val isIdle = state.phase == BreathingPhase.IDLE

        setTileState(
            state = if (isIdle) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE,
            label = breathingLabelProvider.getLabel(state.phase),
            subtitle = breathingLabelProvider.getSubtitle(state.phase),
            icon = breathingIconProvider.getIcon(state.phase, state.progress)
        )
    }
}
package com.wstxda.toolkit.tiles.brightness

import android.service.quicksettings.Tile
import com.wstxda.toolkit.activity.WriteSettingsPermissionActivity
import com.wstxda.toolkit.base.BaseTileService
import com.wstxda.toolkit.manager.brightness.AutoBrightnessManager
import com.wstxda.toolkit.ui.icon.AutoBrightnessIconProvider
import com.wstxda.toolkit.ui.label.AutoBrightnessLabelProvider
import kotlinx.coroutines.flow.Flow

class AutoBrightnessTileService : BaseTileService() {

    private val brightnessManager by lazy { AutoBrightnessManager(applicationContext) }
    private val brightnessLabelProvider by lazy { AutoBrightnessLabelProvider(applicationContext) }
    private val brightnessIconProvider by lazy { AutoBrightnessIconProvider(applicationContext) }

    override fun onStartListening() {
        super.onStartListening()
        brightnessManager.start()
    }

    override fun onStopListening() {
        super.onStopListening()
        brightnessManager.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        brightnessManager.cleanup()
    }

    override fun onClick() {
        if (brightnessManager.isPermissionGranted()) {
            brightnessManager.toggle()
        } else {
            startActivityAndCollapse(WriteSettingsPermissionActivity::class.java)
        }
    }

    override fun flowsToCollect(): List<Flow<*>> {
        return listOf(brightnessManager.isEnabled)
    }

    override fun updateTile() {
        val isActive = brightnessManager.isEnabled.value
        val hasPermission = brightnessManager.isPermissionGranted()

        setTileState(
            state = if (isActive && hasPermission) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE,
            label = brightnessLabelProvider.getLabel(),
            subtitle = brightnessLabelProvider.getSubtitle(isActive, hasPermission),
            icon = brightnessIconProvider.getIcon(isActive)
        )
    }
}
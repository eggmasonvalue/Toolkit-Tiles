package com.wstxda.toolkit.tiles.mediaoutput

import android.service.quicksettings.Tile
import com.wstxda.toolkit.activity.MediaOutputActivity
import com.wstxda.toolkit.base.BaseTileService
import com.wstxda.toolkit.ui.icon.MediaOutputIconProvider
import com.wstxda.toolkit.ui.label.MediaOutputLabelProvider
import kotlinx.coroutines.flow.Flow

class MediaOutputTileService : BaseTileService() {

    private val mediaOutputLabelProvider by lazy { MediaOutputLabelProvider(applicationContext) }
    private val mediaOutputIconProvider by lazy { MediaOutputIconProvider(applicationContext) }

    override fun onClick() {
        startActivityAndCollapse(MediaOutputActivity::class.java)
    }

    override fun flowsToCollect(): List<Flow<*>> {
        return emptyList()
    }

    override fun updateTile() {

        setTileState(
            state = Tile.STATE_INACTIVE,
            label = mediaOutputLabelProvider.getLabel(),
            subtitle = mediaOutputLabelProvider.getSubtitle(),
            icon = mediaOutputIconProvider.getIcon()
        )
    }
}
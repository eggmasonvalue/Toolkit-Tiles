package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R

class AutoBrightnessLabelProvider(private val context: Context) {

    fun getLabel(): CharSequence {
        return context.getString(R.string.auto_brightness_tile)
    }

    fun getSubtitle(isActive: Boolean, hasPermission: Boolean): CharSequence {
        if (!hasPermission) {
            return context.getString(R.string.tile_setup)
        }

        if (isActive) {
            return context.getString(R.string.tile_on)
        }

        return context.getString(R.string.tile_off)
    }
}
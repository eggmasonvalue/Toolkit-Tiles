package com.wstxda.toolkit.manager.brightness

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AutoBrightnessManager(context: Context) {

    private val appContext = context.applicationContext
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled = _isEnabled.asStateFlow()
    private var isListening = false

    private val settingsObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            onChange(selfChange, null)
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            fetchCurrentState()
        }
    }

    fun startListening() {
        if (!isListening) {
            fetchCurrentState()
            appContext.contentResolver.registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE),
                true,
                settingsObserver
            )
            isListening = true
        }
    }

    fun stopListening() {
        if (isListening) {
            appContext.contentResolver.unregisterContentObserver(settingsObserver)
            isListening = false
        }
    }

    fun cleanup() {
        stopListening()
        managerScope.cancel()
    }

    fun isPermissionGranted(): Boolean {
        return Settings.System.canWrite(appContext)
    }

    fun toggle() {
        managerScope.launch {
            if (!isPermissionGranted()) return@launch

            val current = isEnabled.value
            val newState = !current
            val success = setSystemMode(newState)

            if (success) {
                _isEnabled.value = newState
            }
        }
    }

    private fun fetchCurrentState() {
        try {
            val mode = Settings.System.getInt(
                appContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE
            )
            val isAuto = mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC

            _isEnabled.value = isAuto
        } catch (_: Exception) {
            _isEnabled.value = false
        }
    }

    private fun setSystemMode(enable: Boolean): Boolean {
        return try {
            val mode = if (enable) {
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            } else {
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            }
            Settings.System.putInt(
                appContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, mode
            )
        } catch (_: Exception) {
            false
        }
    }
}
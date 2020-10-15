package com.cexchanger.cexchanger.util

import android.content.Context
import android.preference.PreferenceManager

class SharePreference(val context: Context) {
    companion object {
        private const val FRIST_INSTALL = "FRIST_INSTALL"
    }

    private val p = PreferenceManager.getDefaultSharedPreferences(context)

    var fristInstall = p.getBoolean(FRIST_INSTALL, false)
        set(value) = p.edit().putBoolean(FRIST_INSTALL, value).apply()
}
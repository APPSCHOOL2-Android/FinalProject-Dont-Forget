package com.test.dontforgetproject.Util

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate


object ThemeUtil {
    const val LIGHT_MODE = "light"
    const val DARK_MODE = "dark"
    fun applyTheme(themeColor: String) {
        when (themeColor) {
            LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
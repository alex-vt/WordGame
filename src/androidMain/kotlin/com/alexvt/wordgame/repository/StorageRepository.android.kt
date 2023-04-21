package com.alexvt.wordgame.repository

import android.annotation.SuppressLint
import android.content.Context
import com.alexvt.wordgame.platform.appContext

actual class StorageRepository {

    private val prefName = "Storage"
    private val sharedPreference = appContext.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    actual fun readEntry(key: String, defaultValue: String): String {
        return sharedPreference.getString(key, defaultValue) ?: defaultValue
    }

    @SuppressLint("ApplySharedPref")
    actual fun writeEntry(key: String, value: String) {
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.commit()
    }

}
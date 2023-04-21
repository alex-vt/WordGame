package com.alexvt.wordgame.repository

import kotlinx.browser.localStorage

actual class StorageRepository {

    actual fun readEntry(key: String, defaultValue: String): String {
        return localStorage.getItem(key) ?: defaultValue
    }

    actual fun writeEntry(key: String, value: String) {
        localStorage.setItem(key, value)
    }

}
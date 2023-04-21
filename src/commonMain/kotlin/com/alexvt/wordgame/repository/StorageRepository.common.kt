package com.alexvt.wordgame.repository

expect class StorageRepository() {

    fun readEntry(key: String, defaultValue: String): String

    fun writeEntry(key: String, value: String)

}
package com.alexvt.wordgame.repository

import com.alexvt.wordgame.platform.loadTextFromResources
import kotlin.math.roundToInt

class NounsRepository {

    private lateinit var commonSingleNounsByFrequency: List<String>

    suspend fun loadWordsFromResources() {
        if (::commonSingleNounsByFrequency.isInitialized) return
        commonSingleNounsByFrequency = loadTextFromResources("common_single_nouns_by_frequency")
            .lines()
    }

    fun getRandomNoun(length: Int, selectionPoolSize: Int): String =
        commonSingleNounsByFrequency
            .take(selectionPoolSize)
            .filter { it.length == length }
            .shuffled()
            .first()

    fun getMostCommon(usePartOfDictionary: Double): List<String> =
        commonSingleNounsByFrequency
            .take((usePartOfDictionary * commonSingleNounsByFrequency.size).roundToInt())

    fun isAllowed(word: String, usePartOfDictionary: Double): Boolean =
        word in commonSingleNounsByFrequency
            .take((usePartOfDictionary * commonSingleNounsByFrequency.size).roundToInt())

}
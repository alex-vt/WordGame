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

    fun hasMatches(substring: String, usePartOfDictionary: Double): Boolean =
        commonSingleNounsByFrequency
            .take((usePartOfDictionary * commonSingleNounsByFrequency.size).roundToInt())
            .filter { it.contains(substring) }
            .isNotEmpty()

    fun isAllowed(word: String, usePartOfDictionary: Double): Boolean =
        word in commonSingleNounsByFrequency
            .take((usePartOfDictionary * commonSingleNounsByFrequency.size).roundToInt())

}
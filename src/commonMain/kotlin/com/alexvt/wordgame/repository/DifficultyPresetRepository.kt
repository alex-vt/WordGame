package com.alexvt.wordgame.repository

import com.alexvt.wordgame.model.DifficultyRecord

class DifficultyPresetRepository() {

    private val easy =
        DifficultyRecord(maxWordLength = 4, maxVocabularyNormalizedSize = 0.1, isCustom = false)
    private val medium =
        DifficultyRecord(maxWordLength = 5, maxVocabularyNormalizedSize = 0.15, isCustom = false)
    private val hard =
        DifficultyRecord(maxWordLength = 6, maxVocabularyNormalizedSize = 0.2, isCustom = false)
    private val ultra =
        DifficultyRecord(maxWordLength = 10, maxVocabularyNormalizedSize = 0.5, isCustom = false)

    fun getDifficultyPresets(): List<DifficultyRecord> =
        listOf(easy, medium, hard, ultra)

    fun getDefaultDifficultyPreset(): DifficultyRecord =
        medium

    fun getDefaultCustomDifficulty(): DifficultyRecord =
        hard.copy(isCustom = true)

}
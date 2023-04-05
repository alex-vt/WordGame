package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.NounsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class LoadWordsFromResourcesUseCase(
    private val nounsRepository: NounsRepository,
) {

    fun execute(coroutineScope: CoroutineScope): StateFlow<Boolean> {
        return MutableStateFlow(false).apply {
            coroutineScope.launch {
                nounsRepository.loadWordsFromResources()
                emit(true)
            }
        }.asStateFlow()
    }

}
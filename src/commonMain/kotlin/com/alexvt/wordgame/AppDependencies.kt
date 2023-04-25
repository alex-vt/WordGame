package com.alexvt.wordgame

import com.alexvt.wordgame.repository.*
import com.alexvt.wordgame.viewmodel.MainNavigationViewModelUseCases
import com.alexvt.wordgame.viewmodel.PauseMenuViewModelUseCases
import com.alexvt.wordgame.viewmodel.WordGameViewModelUseCases
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class AppScope

@AppScope
@Component
abstract class AppDependencies {

    abstract val mainNavigationViewModelUseCases: MainNavigationViewModelUseCases
    abstract val pauseMenuViewModelUseCases: PauseMenuViewModelUseCases
    abstract val wordGameViewModelUseCases: WordGameViewModelUseCases


    @AppScope
    @Provides
    protected fun navigationStateRepository(): NavigationStateRepository =
        NavigationStateRepository()

    @AppScope
    @Provides
    protected fun nounsRepository(): NounsRepository = NounsRepository()

    @AppScope
    @Provides
    protected fun storageRepository(): StorageRepository = StorageRepository()

    @AppScope
    @Provides
    protected fun colorThemeRepository(): ColorThemeRepository = ColorThemeRepository()

    @AppScope
    @Provides
    protected fun difficultyPresetRepository(): DifficultyPresetRepository =
        DifficultyPresetRepository()
}

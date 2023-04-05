package com.alexvt.wordgame

import android.app.Application
import com.alexvt.wordgame.platform.appContext

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {
        val dependencies: AppDependencies = AppDependencies::class.create()
    }

}

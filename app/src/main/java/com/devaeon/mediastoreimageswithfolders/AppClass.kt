package com.devaeon.mediastoreimageswithfolders

import android.app.Application
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.devaeon.mediastoreimageswithfolders.ui.MainActivity
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppClass : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this@AppClass

        startKoin {
            androidContext(getContext())
            modules(appModules)
        }


        CaocConfig.Builder.create().restartActivity(MainActivity::class.java).apply()
    }

    companion object {
        private var instance: AppClass? = null

        fun getContext(): AppClass {
            return instance!!
        }
    }
}
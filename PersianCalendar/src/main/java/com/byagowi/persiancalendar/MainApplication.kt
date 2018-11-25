package com.byagowi.persiancalendar

import com.byagowi.persiancalendar.di.DaggerAppComponent
import com.byagowi.persiancalendar.util.Utils

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class MainApplication : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        ReleaseDebugDifference.mainApplication(this)
        Utils.initUtils(applicationContext)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}

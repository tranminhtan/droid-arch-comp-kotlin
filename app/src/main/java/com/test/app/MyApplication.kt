package com.test.app

import com.test.app.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class MyApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}
package com.test.app.di

import android.app.Application
import com.test.app.MyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        JsonAdapterModule::class,
        ActivityBindingModule::class]
)
interface AppComponent : AndroidInjector<MyApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}
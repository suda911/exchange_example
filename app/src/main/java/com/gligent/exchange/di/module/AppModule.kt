package com.gligent.exchange.di.module

import android.app.Application
import com.gligent.exchange.MyApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val mApplication: Application) {

    @Provides
    @Singleton
    internal fun providesApplication(): Application {
        return mApplication
    }

    @Provides
    @Singleton
    internal fun providesMyApplication(): MyApp {
        return mApplication as MyApp
    }

}
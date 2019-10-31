package com.gligent.exchange.di.module

import com.gligent.exchange.MyApp
import com.gligent.exchange.data.models.Currency
import dagger.Module
import dagger.Provides
import io.objectbox.Box
import javax.inject.Singleton

@Module
class ObjectBoxModule {

    @Provides
    @Singleton
    fun provideCurrencyBox(myApp: MyApp): Box<Currency> {
        return myApp.boxStore.boxFor(Currency::class.java)
    }

}
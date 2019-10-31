package com.gligent.exchange.di.module

import com.gligent.exchange.domain.repository.CurrencyRepository
import com.gligent.exchange.domain.repository.CurrencyRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    fun provideCurrencyRepository(currencyRepositoryImpl: CurrencyRepositoryImpl): CurrencyRepository
}
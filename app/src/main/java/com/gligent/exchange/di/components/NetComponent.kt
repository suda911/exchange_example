package com.gligent.exchange.di.components


import com.gligent.exchange.di.module.AppModule
import com.gligent.exchange.di.module.NetModule
import com.gligent.exchange.di.module.ObjectBoxModule
import com.gligent.exchange.di.module.RepositoryModule
import com.gligent.exchange.domain.service.CurrencyService
import com.gligent.exchange.ui.deposit.BaseDepositFragment
import com.gligent.exchange.ui.exchange.ExchangeActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AppModule::class,
        NetModule::class,
        ObjectBoxModule::class,
        RepositoryModule::class
    ]
)
interface NetComponent {
    fun inject(exchangeActivity: ExchangeActivity)
    fun inject(exchangeActivity: CurrencyService)
    fun inject(baseDepositFragment: BaseDepositFragment)

}
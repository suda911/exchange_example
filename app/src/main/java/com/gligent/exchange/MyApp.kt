package com.gligent.exchange

import android.app.Application
import android.content.Intent

import com.gligent.exchange.data.models.MyObjectBox
import com.gligent.exchange.di.components.DaggerNetComponent
import com.gligent.exchange.di.components.NetComponent
import com.gligent.exchange.di.module.AppModule
import com.gligent.exchange.di.module.NetModule
import com.gligent.exchange.domain.service.CurrencyService

import io.objectbox.BoxStore


class MyApp : Application() {
    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()

        netComponent = DaggerNetComponent.builder()
            .appModule(AppModule(this))
            .netModule(NetModule(BuildConfig.BASE_URL))
            .build()

        startService(Intent(this, CurrencyService::class.java))
    }

    companion object {
        lateinit var netComponent: NetComponent
            private set
    }

}
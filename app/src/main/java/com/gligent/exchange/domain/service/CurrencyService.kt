package com.gligent.exchange.domain.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import com.gligent.exchange.ApiCurrency
import com.gligent.exchange.MyApp
import com.gligent.exchange.domain.repository.CurrencyRepository
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CurrencyService : Service() {

    @Inject
    lateinit var currencyRepository: CurrencyRepository

    @Inject
    lateinit var apiCurrency: ApiCurrency

    private lateinit var disposable: Disposable

    override fun onBind(p0: Intent?) = Binder()

    override fun onCreate() {
        super.onCreate()
        MyApp.netComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (::disposable.isInitialized) {
            disposable.dispose()
        }

        disposable = apiCurrency.getCurrency()
            .subscribeOn(Schedulers.io())
            .repeatWhen { t -> t.delay(30, TimeUnit.SECONDS) }
            .subscribe({
                currencyRepository.setCurrency(it)
            },
                {
                    startService(Intent(this, CurrencyService::class.java))
                }
            )

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
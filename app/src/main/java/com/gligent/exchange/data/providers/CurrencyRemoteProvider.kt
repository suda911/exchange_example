package com.gligent.exchange.data.providers

import com.gligent.exchange.ApiCurrency
import com.gligent.exchange.data.models.Rates
import com.gligent.exchange.data.models.Result
import javax.inject.Inject

class CurrencyRemoteProvider @Inject constructor(private val apiCurrency: ApiCurrency) {

    suspend fun getCurrency(): Result<Rates> {
        val value = apiCurrency.getRates()

        val result = Result<Rates>()
        if (value.isSuccessful) {
            result.data = value.body()
        } else {
            result.error = "Данных нет, но вы держитесь"
        }

        return result
    }
}
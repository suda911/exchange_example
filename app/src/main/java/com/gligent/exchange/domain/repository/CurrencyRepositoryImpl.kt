package com.gligent.exchange.domain.repository

import com.gligent.exchange.data.models.Currency
import com.gligent.exchange.data.models.Rates
import com.gligent.exchange.data.providers.CurrencyLocalProvider
import com.gligent.exchange.data.providers.CurrencyRemoteProvider
import io.objectbox.reactive.DataObserver
import io.objectbox.reactive.DataSubscription
import javax.inject.Inject

class CurrencyRepositoryImpl
@Inject constructor(
    private val remote: CurrencyRemoteProvider,
    private val local: CurrencyLocalProvider
) : CurrencyRepository {


    override suspend fun getCurrency(): List<Currency> {
        val result = remote.getCurrency()

        return if (result.error.isNullOrEmpty()) {
            try {
                val list = result.data?.rates?.entries?.map { entry ->
                    Currency(ticket = entry.key, course = entry.value)
                }

                if (list != null) {
                    local.setList(list)
                }

                local.getCurrency()
            } catch (ex: Exception) {
                local.getCurrency()
            }

        } else {
            local.getCurrency()
        }
    }

    override fun setCurrency(rates: Rates?) {
        rates?.let {
            val list = it.rates.entries.map { entry ->
                Currency(ticket = entry.key, course = entry.value)
            }

            local.setList(list)
        }
    }

    override fun subscribeToDeposit(
        observer: DataObserver<Currency?>,
        ticket: String
    ): DataSubscription {
        return local.subscribeToDeposit(observer, ticket)
    }

    override fun subscribeToCurrency(observer: DataObserver<List<Currency>>): DataSubscription {
        return local.subscribeToCurrency(observer)
    }

    override fun updateDeposit(
        ticketBasic: String,
        valueBasic: Double,
        ticketOther: String,
        valueOther: Double
    ): Boolean {
        return local.updateDeposit(ticketBasic, valueBasic, ticketOther, valueOther)
    }

    override suspend fun getCurrency(ticket: String): Currency? {
        return local.getCurrency(ticket)
    }
}
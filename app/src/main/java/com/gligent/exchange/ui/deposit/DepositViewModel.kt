package com.gligent.exchange.ui.deposit

import android.app.Application
import androidx.databinding.ObservableField
import com.gligent.exchange.data.models.Currency
import com.gligent.exchange.domain.repository.CurrencyRepository
import com.gligent.exchange.ui.viewmodel.BaseViewModel
import io.objectbox.reactive.DataObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DepositViewModel(
    application: Application,
    private val currencyRepository: CurrencyRepository
) : BaseViewModel(application) {

    val deposit = ObservableField<String>()

    /**
     * Поиск депозита по тикету
     * */
    fun initDeposit(ticket: String) {
        CoroutineScope(coroutineContext).launch {
            currencyRepository.subscribeToDeposit(DataObserver {
                setDeposit(currency = it)
            }, ticket).also {
                addSubscription(it)
            }
        }
    }

    /**
     * установка значения депозита в liveData
     * */
    fun setDeposit(currency: Currency?) {
        deposit.set(currency?.getMyDeposit())
    }
}
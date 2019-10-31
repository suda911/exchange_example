package com.gligent.exchange.ui.deposit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.gligent.exchange.MyApp
import com.gligent.exchange.domain.repository.CurrencyRepository
import com.gligent.exchange.ui.exchange.ExchangeViewModel
import com.gligent.exchange.ui.viewmodel.factory
import javax.inject.Inject

abstract class BaseDepositFragment : Fragment() {

    @Inject
    lateinit var currencyRepository: CurrencyRepository

    /**
     * ViewModel для обмена данными
     * */
    val viewModel by lazy {
        val factory = factory {
            ExchangeViewModel(requireActivity().application, currencyRepository)
        }

        ViewModelProviders.of(requireActivity(), factory).get(ExchangeViewModel::class.java)
    }

    /**
     * ViewModel для доступа к депозиту
     * */
    val depositViewModel by lazy {
        val factory = factory {
            DepositViewModel(requireActivity().application, currencyRepository)
        }

        ViewModelProviders.of(this, factory).get(DepositViewModel::class.java)
    }

    /**
     * Инжект зависимостей
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApp.netComponent.inject(this)
    }

    /**
     * Метод для установки слушателей
     * */
    abstract fun setListeners()

    /**
     * Метод для удаления слушателей
     * */
    abstract fun removeListeners()

    /**
     * Метод для установки тикета валюты
     * */
    abstract fun setTicket(ticket: String)

    /**
     * Метод для обновления количества валюты
     * */
    abstract fun changeAmount(amount: String)

    override fun onResume() {
        super.onResume()
        setListeners()
    }

    override fun onPause() {
        removeListeners()
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments!!.let {
            val ticket = it.getString("currency").toString()
            setTicket(ticket)
            depositViewModel.initDeposit(ticket)
        }
    }

    /**
     * Слушатель для изменения кол-ва валюты
     * */
    val textWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            changeAmount(s.toString())
        }
    }
}
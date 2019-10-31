package com.gligent.exchange.ui.exchange

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.gligent.exchange.MyApp
import com.gligent.exchange.R
import com.gligent.exchange.data.models.ConvertAmountPair
import com.gligent.exchange.data.models.Currency
import com.gligent.exchange.domain.convert
import com.gligent.exchange.domain.repository.CurrencyRepository
import com.gligent.exchange.ui.viewmodel.BaseViewModel
import io.objectbox.reactive.DataObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ExchangeViewModel(
    application: Application,
    private val currencyRepository: CurrencyRepository
) : BaseViewModel(application) {

    val currencyList = MutableLiveData<List<Currency>>()

    private val list: List<Currency>?
        get() = currencyList.value

    private val basicCurrency = ObservableField<Currency>()
    private val otherCurrency = ObservableField<Currency>()

    val basicCourse = ObservableField<String>()
    val otherCourse = ObservableField<String>()

    val basicAmount = MutableLiveData<String>()
    val otherAmount = MutableLiveData<String>()

    private val convertPair = ConvertAmountPair()

    private val focusAmount = ObservableField<FOCUS>()

    private val focus: FOCUS
        get() = focusAmount.get() ?: FOCUS.BASIC

    init {
        getCurrency()
    }

    fun getCurrency() {
        CoroutineScope(coroutineContext).launch {
            currencyRepository.subscribeToCurrency(DataObserver { data -> setCurrency(data) })
        }
    }

    /**
     * Установка списка валют в liveData для отображения на главном экране
     * */
    fun setCurrency(list: List<Currency>?) {
        if (list == null) return

        if(this@ExchangeViewModel.list != null){
            return
        }

        if (list.isNotEmpty()) {
            currencyList.value = list
            setPrimaryCurrency(0)
            setSecondaryCurrency(0)
            exchangeCourse()
        } else {
            toast.value = getApplication<MyApp>().getString(R.string.download_exception)
        }
    }

    /**
     * Метод для расчитывания курсов валют в формат 1E = 0.5$
     * */
    fun exchangeCourse() {
        val basicCurrency = basicCurrency.get()
        val otherCurrency = otherCurrency.get()

        if (basicCurrency == null || otherCurrency == null) {
            toast.value = getApplication<MyApp>().getString(R.string.error_convert)
            return
        }

        //установка курса для основной валюты
        basicCourse.set(
            "1 = " + convert(
                otherCurrency.course,
                1.0,
                basicCurrency.course
            ).toString()
        )

        //установка курса для побочной валюты
        otherCourse.set(
            "1 = " + convert(
                basicCurrency.course,
                1.0,
                otherCurrency.course
            ).toString()

        )
    }

    /**
     * Установка базовой валюты
     * */
    fun setPrimaryCurrency(position: Int) {
        clearingAmount()
        list?.let {
            val currency = it[position]
            basicCurrency.set(currency)
        }
    }

    /**
     * Установка валюты в которую будет произведён обмен
     * */
    fun setSecondaryCurrency(position: Int) {
        clearingAmount()
        list?.let {
            val currency = it[position]
            otherCurrency.set(currency)
        }
    }

    /**
     * Очистка полей из editText в карточке счёта
     * Очистка observableFields
     * */
    private fun clearingAmount() {
        basicAmount.value = null
        otherAmount.value = null
    }

    /**
     * Метод для установки конвертируемого кол-ва основой валюты и конвертация пары
     * @param amount - кол-во основной валюты
     * */
    fun setBasicAmount(amount: String?) {
        if (focus == FOCUS.OTHER) return

        if (amount.isNullOrEmpty()) {
            otherAmount.value = null
            convertPair.clear()
            return
        }

        val value: BigDecimal

        try {
            value =
                BigDecimal(amount)
        } catch (nfe: NumberFormatException) {
            return
        }

        val basicCurrency = basicCurrency.get() ?: return
        val otherCurrency = otherCurrency.get() ?: return

        val convertingAmount = convert(
            otherCurrency.course,
            value.toDouble(),
            basicCurrency.course
        )

        convertPair.basicAmount = amount
        convertPair.otherAmount = convertingAmount.toString()

        otherAmount.value = convertingAmount.toString()

    }

    /**
     * Метод для установки конвертируемого кол-ва получаемой валюты и конвертация пары
     * @param amount - кол-во побочной валюты
     * */
    fun setOtherAmount(amount: String?) {
        if (focus == FOCUS.BASIC) return

        if (amount.isNullOrEmpty()) {
            basicAmount.value = null
            convertPair.clear()
            return
        }

        val value: BigDecimal

        try {
            value =
                BigDecimal(amount)
        } catch (nfe: NumberFormatException) {
            return
        }

        val basicCurrency = basicCurrency.get() ?: return
        val otherCurrency = otherCurrency.get() ?: return

        val convertingAmount = convert(
            basicCurrency.course,
            value.toDouble(),
            otherCurrency.course
        )

        convertPair.otherAmount = amount
        convertPair.basicAmount = convertingAmount.toString()

        basicAmount.value = convertingAmount.toString()

    }

    /**
     * Определение фокуса клавтатуры
     * */
    fun setFocus(focus: FOCUS) {
        focusAmount.set(focus)
    }


    /***
     *
     * Произведение конвертации и запись в базу данных
     *
     * */
    fun exchange() {
        CoroutineScope(coroutineContext).launch {
            val basicCurrency = basicCurrency.get() ?: return@launch
            val otherCurrency = otherCurrency.get() ?: return@launch


            if (basicCurrency.ticket == otherCurrency.ticket) {
                toast.value = getApplication<MyApp>().getString(R.string.error_one)
                return@launch
            }


            if (!convertPair.validate()) {
                toast.value = getApplication<MyApp>().getString(R.string.need_set_amount)
                return@launch
            }


            val basicAmount = convertPair.basicAmount?.toDouble() ?: return@launch


            if (basicAmount <= 0) {
                toast.value = getApplication<MyApp>().getString(R.string.sum_is_negative)
                return@launch
            }

            if (!validateDeposit(ticket = basicCurrency.ticket, value = basicAmount)) {
                toast.value = getApplication<MyApp>().getString(R.string.balance_is_empty)
                return@launch
            }

            val subtractValue = BigDecimal(convertPair.basicAmount).negate()

            val otherAmount = convertPair.otherAmount?.toDouble() ?: return@launch

            if (otherAmount <= 0) {
                toast.value = getApplication<MyApp>().getString(R.string.sum_is_negative)
                return@launch
            }

            val addValue = BigDecimal(convertPair.otherAmount)

            val resultTransaction = currencyRepository.updateDeposit(
                basicCurrency.ticket,
                subtractValue.toDouble(),
                otherCurrency.ticket,
                addValue.toDouble()
            )

            if (resultTransaction) {
                toast.value = getApplication<MyApp>().getString(R.string.transaction_success)
                clearingAmount()
            } else {
                toast.value = getApplication<MyApp>().getString(R.string.transaction_failed)
            }

            convertPair.clear()
        }

    }

    /**
     * Определение наличия средств на балансе
     * @param ticket - тикет валюты
     * @param value - кол-во которое нужно будет отнять
     * */
    suspend fun validateDeposit(ticket: String, value: Double): Boolean {
        return CoroutineScope(coroutineContext).async {
            val currency = currencyRepository.getCurrency(ticket) ?: return@async false
            return@async currency.deposit >= value
        }.await()
    }

    /**
     * Перечисления для определения фокуса клавиатуры на экране
     * */
    enum class FOCUS {
        BASIC, OTHER
    }

}
package com.gligent.exchange.data.providers

import com.gligent.exchange.data.models.Currency
import com.gligent.exchange.data.models.Currency_
import io.objectbox.Box
import io.objectbox.android.AndroidScheduler
import io.objectbox.reactive.DataObserver
import io.objectbox.reactive.DataSubscription
import io.objectbox.reactive.SubscriptionBuilder
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class CurrencyLocalProvider @Inject constructor(private val db: Box<Currency>) {

    /**
     * Сохранение списка валют
     * */
    private fun setCurrency(ticket: String, course: Double) {
        val currency = db.query().equal(Currency_.ticket, ticket).build().findFirst()

        if (currency == null) {
            db.put(Currency(ticket = ticket, course = course))
        } else {
            currency.course = course
            db.put(currency)
        }
    }

    /**
     * Сохранение в бд списка валют
     * */
    fun setList(list: List<Currency>) {
        if (db.count() == 0L) {
            db.put(list)
        }

        for (currency in list) {
            setCurrency(currency.ticket, currency.course)
        }
    }

    /**
     * Обновление депозитов валют
     * */
    fun updateDeposit(
        ticketBasic: String,
        valueBasic: Double,
        ticketOther: String,
        valueOther: Double
    ): Boolean {

        if (ticketBasic == ticketOther) {
            return false
        }

        val depositBasic = db.query()
            .equal(Currency_.ticket, ticketBasic)
            .build()
            .findUnique()

        val depositOther = db.query()
            .equal(Currency_.ticket, ticketOther)
            .build()
            .findUnique()

        if (depositBasic == null || depositOther == null) {
            return false
        }

        return try {

            depositBasic.deposit =
                BigDecimal(depositBasic.deposit).add(
                    BigDecimal(valueBasic).setScale(
                        2,
                        RoundingMode.CEILING
                    )
                ).setScale(2, RoundingMode.CEILING).toDouble()

            depositOther.deposit =
                BigDecimal(depositOther.deposit).add(
                    BigDecimal(valueOther).setScale(
                        2,
                        RoundingMode.CEILING
                    )
                ).setScale(2, RoundingMode.CEILING).toDouble()

            //сохраняем в бд
            db.put(depositBasic, depositOther)

            true
        } catch (ex: Exception) {
            false
        }
    }

    /**
     * Получение списка валют
     * */
    fun getCurrency(): List<Currency> {
        return db.all.toList()
    }

    /**
     * Созадание подписка на депозит
     * */
    fun subscribeToDeposit(observer: DataObserver<Currency?>, ticket: String): DataSubscription {
        val builder: SubscriptionBuilder<Currency?> = db.query().equal(Currency_.ticket, ticket)
            .build()
            .subscribe()
            .transform { list -> list.singleOrNull() }
            .on(AndroidScheduler.mainThread())

        return builder.observer(observer)
    }

    /**
     * Создание одиночной подписки списка валют
     * */
    fun subscribeToCurrency(observer: DataObserver<List<Currency>>): DataSubscription {
        return db.query().build().subscribe().on(AndroidScheduler.mainThread()).onlyChanges()
            .observer(observer)
    }

    /**
     * Получение одной валюты
     * */
    fun getCurrency(ticket: String): Currency? {
        return db.query().equal(Currency_.ticket, ticket).build().findUnique()
    }
}
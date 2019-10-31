package com.gligent.exchange.domain.repository

import com.gligent.exchange.data.models.Currency
import com.gligent.exchange.data.models.Rates
import io.objectbox.reactive.DataObserver
import io.objectbox.reactive.DataSubscription

interface CurrencyRepository {
    /**
     * Получения списка валют
     * */
    suspend fun getCurrency(): List<Currency>

    /**
     * Сохранение списка валют
     * @param rates тело запроса из сети
     * */
    fun setCurrency(rates: Rates?)

    /**
     * Создание подписки для получения депозита
     * @param observer - подписчик для получения депозита
     * */
    fun subscribeToDeposit(observer: DataObserver<Currency?>, ticket: String): DataSubscription

    /**
     * Создание подписки для получения всех валют
     * @param observer - подписчик для получения списка
     * */
    fun subscribeToCurrency(observer: DataObserver<List<Currency>>): DataSubscription


    /**
     * Обновления депозитов
     * */
    fun updateDeposit(
        ticketBasic: String,
        valueBasic: Double,
        ticketOther: String,
        valueOther: Double
    ): Boolean


    /**
     * Получения одного депозита
     * @param ticket - тикет валюты
     * */
    suspend fun getCurrency(ticket: String): Currency?
}
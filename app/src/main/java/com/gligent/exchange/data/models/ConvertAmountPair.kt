package com.gligent.exchange.data.models

/**
 * Объект для хранения кол-ва валют
 * */
data class ConvertAmountPair(var basicAmount: String? = null, var otherAmount: String? = null) {
    /**
     * Проверка на валидность кол-ва каждой валюты
     * */
    fun validate(): Boolean {
        return basicAmount?.isNotEmpty() ?: false && otherAmount?.isNotEmpty() ?: false
    }

    /**
     * Очистка данных
     * */
    fun clear(){
        basicAmount = null
        otherAmount = null
    }
}
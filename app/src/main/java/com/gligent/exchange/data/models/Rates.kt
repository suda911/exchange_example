package com.gligent.exchange.data.models

/**
 * Модель ответа на запрос за валютами
 * */
data class Rates(
    val rates: Map<String, Double>,
    val base: String,
    val date: String
)
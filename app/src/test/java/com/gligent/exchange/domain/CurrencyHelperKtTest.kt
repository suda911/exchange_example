package com.gligent.exchange.domain

import org.junit.Test

import org.junit.Assert.*

class CurrencyHelperKtTest {

    //базовая валюта евро

    @Test
    // GBP to RUB
    fun convertRUBtoGBP() {
        val result = convert(0.86328, 1.0, 70.5328)
        assertEquals(0.02, result, 0.001)
    }

    @Test
    // GBP to GBP
    fun convertGBPtoGBP() {
        val result = convert(0.86328, 1.0, 0.86328)
        assertEquals(1.0, result, 0.0)
    }

    @Test
    // GBP to USD
    fun convertUSDtoGBP() {
        val result = convert(0.86328, 1.0, 1.1087)
        assertEquals(0.78, result, 0.001)
    }

    @Test
    // USD to KRW
    fun convertUSDtoKRW() {
        val result = convert(1297.1, 1.0, 1.1087)
        assertEquals(1169.76, result, 0.20)
    }
}
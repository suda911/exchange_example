package com.gligent.exchange.domain

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * [Сумма в валюте конвертации(2)]=[Сумма в валюте конвертации(1)]*[Кратность(1)]*[Курс(2)]/[Кратность(2)]*[Курс(1)]
 *
 */
fun convert(basicCourse: Double, amount: Double, otherCourse: Double): Double {
    return BigDecimal(amount).multiply(BigDecimal(basicCourse))
        .divide(BigDecimal(otherCourse), 2, RoundingMode.CEILING)
        .toDouble()
}
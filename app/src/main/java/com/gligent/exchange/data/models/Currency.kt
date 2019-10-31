package com.gligent.exchange.data.models

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Медель для валюты
 * */
@Entity
data class Currency(
    @Id(assignable = true) var id: Long = 0,
    var ticket: String,
    var course: Double,
    var deposit: Double = 100.0
) {
    fun getMyDeposit(): String {
        return "You have : $deposit"
    }
}
package com.gligent.exchange.data.models

/***
 * Обёртка над ответом
 */
class Result<T> {
    var data: T? = null
    var error: String? = null
}
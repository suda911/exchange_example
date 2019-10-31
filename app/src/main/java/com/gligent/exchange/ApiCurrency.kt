package com.gligent.exchange

import com.gligent.exchange.data.models.Rates
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface ApiCurrency {

    @GET("latest")
    suspend fun getRates(): Response<Rates>

    @GET("latest")
    fun getCurrency(): Observable<Rates>
}
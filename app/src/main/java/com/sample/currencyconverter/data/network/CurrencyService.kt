package com.sample.currencyconverter.data.network

import com.sample.currencyconverter.model.ExchangeRates
import retrofit2.Response
import retrofit2.http.GET

interface CurrencyService {
    @GET("latest.json")
    suspend fun getExhangeRates(): Response<ExchangeRates>
}
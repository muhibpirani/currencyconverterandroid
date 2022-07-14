package com.sample.currencyconverter.data.remote

import com.sample.currencyconverter.data.network.CurrencyService
import com.sample.currencyconverter.model.ExchangeRates
import retrofit2.Response
import javax.inject.Inject

/**
 * This class would handle all the remote api calls
 */
class CurrencyRemoteData @Inject constructor(private val currencyService: CurrencyService) {

    suspend fun getExchangeRate(): Response<ExchangeRates> {
        return currencyService.getExhangeRates()
    }
}
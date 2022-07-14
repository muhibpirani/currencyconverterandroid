package com.sample.currencyconverter.data.repository

import com.sample.currencyconverter.model.ExchangeRates
import kotlinx.coroutines.flow.Flow

/**
 * Single entry point for repository
 */
interface CurrencyRepository {
    suspend fun getExchangeRates(): Flow<Resource<ExchangeRates>>
}
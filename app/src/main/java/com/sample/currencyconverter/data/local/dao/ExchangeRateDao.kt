package com.sample.currencyconverter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.currencyconverter.model.ExchangeRates
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(exchangeRate: ExchangeRates)

    @Query("SELECT * FROM ${ExchangeRates.TABLE_NAME}")
    fun getExchangeRateData(): Flow<ExchangeRates>
}
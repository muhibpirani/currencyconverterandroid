package com.sample.currencyconverter.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sample.currencyconverter.data.local.converter.MapTypeConverter
import com.sample.currencyconverter.model.ExchangeRates.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class ExchangeRates(
    var disclaimer: String? = null,
    var license: String? = null,
    var timestamp: Long? = null,
    var base: String? = null,
    @PrimaryKey
    var primaryId: String = "NA",

    @TypeConverters(MapTypeConverter::class)
    var rates: HashMap<String, Double>? = null
) {
    companion object {
        const val TABLE_NAME = "exchange_rate"
    }
}
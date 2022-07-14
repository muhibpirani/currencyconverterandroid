package com.sample.currencyconverter.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sample.currencyconverter.data.local.converter.MapTypeConverter
import com.sample.currencyconverter.data.local.dao.ExchangeRateDao
import com.sample.currencyconverter.model.ExchangeRates

@Database(
    entities = [ExchangeRates::class],
    version = DatabaseMigrations.DB_VERSION
)

@TypeConverters(MapTypeConverter::class)
abstract class CurrencyDatabase : RoomDatabase() {

    /**
     * @return [DAO] Posts Data Access Object.
     */
    abstract fun getExchangeRateDao(): ExchangeRateDao

    companion object {
        const val DB_NAME = "sample_database"

        @Volatile
        private var INSTANCE: CurrencyDatabase? = null

        fun getInstance(context: Context): CurrencyDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyDatabase::class.java,
                    DB_NAME
                ).addMigrations(*DatabaseMigrations.MIGRATIONS).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
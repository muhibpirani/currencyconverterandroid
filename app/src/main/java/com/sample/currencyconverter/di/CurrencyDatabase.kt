package com.sample.currencyconverter.di

import android.app.Application
import com.sample.currencyconverter.data.local.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CurrencyDatabase {

    @Singleton
    @Provides
    fun provideDatabase(application: Application) = CurrencyDatabase.getInstance(application)

    @Singleton
    @Provides
    fun provideExchangeRateDao(database: CurrencyDatabase) = database.getExchangeRateDao()

}
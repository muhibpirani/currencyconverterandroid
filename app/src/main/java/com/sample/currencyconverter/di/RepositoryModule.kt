package com.sample.currencyconverter.di

import com.sample.currencyconverter.data.repository.CurrencyRepository
import com.sample.currencyconverter.data.repository.CurrencyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun providesRepositoryModule(currencyRepositoryImpl : CurrencyRepositoryImpl) : CurrencyRepository
}
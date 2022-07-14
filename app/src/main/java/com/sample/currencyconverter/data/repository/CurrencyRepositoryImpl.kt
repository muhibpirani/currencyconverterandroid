package com.sample.currencyconverter.data.repository

import com.sample.currencyconverter.Constants.THIRTY_MINUTES
import com.sample.currencyconverter.data.local.dao.ExchangeRateDao
import com.sample.currencyconverter.data.network.CurrencyService
import com.sample.currencyconverter.data.remote.CurrencyRemoteData
import com.sample.currencyconverter.model.ExchangeRates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Single entry point to manage repository
 */
class CurrencyRepositoryImpl @Inject constructor(
    private val currencyRemoteData: CurrencyRemoteData,
    private val exchangeRateDao: ExchangeRateDao,
    private val currencyService: CurrencyService,
    private val ioDispatcher : CoroutineContext
) : CurrencyRepository {

    override suspend fun getExchangeRates(): Flow<Resource<ExchangeRates>> {

        return object : NetworkBoundResource<ExchangeRates, ExchangeRates>() {
            override suspend fun saveNetworkResult(item: ExchangeRates) {
                item.timestamp =
                    System.currentTimeMillis() // changing it to local systems milliseconds
                exchangeRateDao.insertExchangeRates(item)
            }

            override fun shouldFetch(data: ExchangeRates?): Boolean {
                // Data would be refreshed after 30 minutes only
                data?.timestamp?.let {
                    val thirtyAgo: Long = System.currentTimeMillis() - THIRTY_MINUTES
                    return it < thirtyAgo
                }
                return true
            }

            override fun loadFromDb(): Flow<ExchangeRates> {
                return exchangeRateDao.getExchangeRateData()
            }

            override suspend fun fetchFromNetwork(): Response<ExchangeRates> {
                return currencyRemoteData.getExchangeRate()
            }

        }.asFlow().flowOn(ioDispatcher)
    }
}
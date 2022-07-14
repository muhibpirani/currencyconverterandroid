package com.sample.currencyconverter.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.currencyconverter.data.repository.CurrencyRepository
import com.sample.currencyconverter.data.repository.Resource
import com.sample.currencyconverter.model.Currency
import com.sample.currencyconverter.model.ExchangeRates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) :
    ViewModel() {
    val selectedBase: MutableLiveData<String> by lazy { MutableLiveData<String>("USD") }
    var selectedBaseValueAgainstUSD: Double = 1.0
    var exchangeRates: ExchangeRates? = null
    val currencyExchangeList: MutableList<Currency> = mutableListOf()
    val currencyExchangeLiveData by lazy { MutableLiveData<List<Currency>>() }

    /**
     * Flow would be useful in case user spends more than 30 minutes on the app itself and API call is required
     * then live update of values could be emitted
     */
    fun getExchangeRate() {
        viewModelScope.launch {
            currencyRepository.getExchangeRates().collect {
                when (it) {
                    is Resource.Success -> {
                        exchangeRates = it.data
                        handleExchangeRateData()
                    }
                    else -> Log.e("ERROR", it.errorMessage ?: "Cannot load data")
                }
            }
        }
    }

    fun handleExchangeRateData() {
        currencyExchangeList.clear()
        exchangeRates?.let {
            it.rates?.toList()?.map {
                val relativeRate = calculateRate(it.second)
                currencyExchangeList.add(
                    Currency(
                        name = it.first,
                        valueAgainstSelected = relativeRate,
                        valueInUsd = it.second,
                        valueAfterMultipledValue = relativeRate // Default to 1
                    )
                )
            }
            currencyExchangeLiveData.postValue(currencyExchangeList.sortedBy { it.name })
        }
    }

    fun calculateRate(usdRate: Double): Double {
        if (selectedBase.value.equals("USD", true))
            return usdRate
        else {
            // for cross values like INR -> EUR
            return usdRate / selectedBaseValueAgainstUSD
        }
    }

    /**
     * To multiply the conversion rate with user entered value
     * @param amount : user entered amount
     */
    fun multiplyAmount(amount: String) {
        val multiplicationBy = try {
            amount.toDouble()
        } catch (exception: Exception) {
            1.0 // default in case of one
        }
        currencyExchangeList.map {
            it.valueAfterMultipledValue = it.valueAgainstSelected?.times(multiplicationBy)
        }
        currencyExchangeLiveData.postValue(currencyExchangeList.sortedBy { it.name })
    }
}
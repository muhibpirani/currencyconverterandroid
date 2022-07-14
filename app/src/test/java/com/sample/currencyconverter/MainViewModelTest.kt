package com.sample.currencyconverter

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sample.currencyconverter.data.repository.CurrencyRepository
import com.sample.currencyconverter.data.repository.Resource
import com.sample.currencyconverter.model.Currency
import com.sample.currencyconverter.model.ExchangeRates
import com.sample.currencyconverter.ui.MainViewModel
import com.sample.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*

class MainViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var currencyRepository: CurrencyRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        mainViewModel = MainViewModel(currencyRepository)
        mockkStatic(Log::class)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `multiply amount invalid double`() {
        val dummyData = getDummyCurrencyItems()
        mainViewModel.currencyExchangeList.addAll(dummyData)
        mainViewModel.multiplyAmount("hey")
        assert(mainViewModel.currencyExchangeList.equals(dummyData))

    }

    @Test
    fun `multiply amount 0`() {
        val dummyData = getDummyCurrencyItems()
        mainViewModel.currencyExchangeList.addAll(dummyData)
        mainViewModel.multiplyAmount("0.0")
        mainViewModel.currencyExchangeList.map {
            Assert.assertEquals(it.valueAfterMultipledValue, 0.0)
        }
    }

    @Test
    fun `multiply amount greater than 0`() {
        val dummyData = getDummyCurrencyItems()
        mainViewModel.currencyExchangeList.addAll(dummyData)
        mainViewModel.multiplyAmount("20.0")
        mainViewModel.currencyExchangeList.map {
            Assert.assertEquals(it.valueAfterMultipledValue, it.valueAgainstSelected?.times(20))
        }
    }

    @Test
    fun `calculate relative rate`() {
        // for USD
        mainViewModel.selectedBase.value = "USD"
        val resultDouble = mainViewModel.calculateRate(1.0)
        Assert.assertEquals(resultDouble, 1.0, 0.1)

        // for usd
        mainViewModel.selectedBase.value = "USD"
        val resultUsdDouble = mainViewModel.calculateRate(1.0)
        Assert.assertEquals(resultUsdDouble, 1.0, 0.1)

        // for other than USD
        mainViewModel.selectedBase.value = "INR"
        mainViewModel.selectedBaseValueAgainstUSD = 79.0
        val inrResult = mainViewModel.calculateRate(20.0)
        Assert.assertEquals(inrResult, 0.2531, 0.2)

    }

    @Test
    fun `get exchange rate failure`() {
        coEvery { currencyRepository.getExchangeRates() } returns flow { emit(Resource.Failed("")) }
        every { Log.e(any(), any()) } returns 0
        mainViewModel.getExchangeRate()
        Assert.assertEquals(mainViewModel.currencyExchangeList.size, 0)
        Assert.assertEquals(mainViewModel.exchangeRates, null)
        Assert.assertEquals(mainViewModel.currencyExchangeLiveData.value, null)
    }

    @Test
    fun `get exchange rate success valid list`() {
        val dummyExchangeRates = getDummyExchangeRateData()
        coEvery { currencyRepository.getExchangeRates() } returns flow {
            emit(
                Resource.Success(
                    dummyExchangeRates
                )
            )
        }
        mainViewModel.getExchangeRate()
        Assert.assertEquals(mainViewModel.currencyExchangeList.size, 3)
        Assert.assertEquals(mainViewModel.exchangeRates, dummyExchangeRates)
        Assert.assertEquals(mainViewModel.currencyExchangeLiveData.getOrAwaitValue().size, 3)
        val sortedList : List<Currency> = mainViewModel.currencyExchangeList.sortedBy { it.name }
        Assert.assertEquals(mainViewModel.currencyExchangeLiveData.getOrAwaitValue()[0].name, "AED")
        Assert.assertEquals(mainViewModel.currencyExchangeLiveData.getOrAwaitValue()[1].name, "INR")
        Assert.assertEquals(mainViewModel.currencyExchangeLiveData.getOrAwaitValue()[2].name, "USD")
        Assert.assertEquals(mainViewModel.currencyExchangeLiveData.getOrAwaitValue(),sortedList)
    }

    @Test
    fun `get exchange rate success empty list`() {
        val emptyExchangeRate = ExchangeRates()
        coEvery { currencyRepository.getExchangeRates() } returns flow {
            emit(
                Resource.Success(
                    emptyExchangeRate
                )
            )
        }
        mainViewModel.getExchangeRate()
        Assert.assertEquals(mainViewModel.currencyExchangeList.size, 0)
        Assert.assertEquals(mainViewModel.currencyExchangeLiveData.value, mainViewModel.currencyExchangeList)
        Assert.assertEquals(mainViewModel.exchangeRates, emptyExchangeRate)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun getDummyCurrencyItems(): MutableList<Currency> {
        val resultList = mutableListOf<Currency>()
        val inrCurrency = Currency("INR", 3.0, 79.0, 3.0)
        val usdCurrency = Currency("AES", 79.0, 3.6, 79.0)
        resultList.add(inrCurrency)
        resultList.add(usdCurrency)
        return resultList
    }

    private fun getDummyExchangeRateData(): ExchangeRates {
        val exchangeRates = ExchangeRates()
        val rates = HashMap<String, Double>()
        rates.put("USD", 1.0)
        rates.put("INR", 79.5)
        rates.put("AED", 3.679)
        exchangeRates.rates = rates

        return exchangeRates
    }

}
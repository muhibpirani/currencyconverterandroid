package com.sample.currencyconverter.ui.defaultcurrencydialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sample.currencyconverter.databinding.FragmentDefaultCurrencySelectorBinding
import com.sample.currencyconverter.model.Currency
import com.sample.currencyconverter.ui.MainViewModel
import com.sample.currencyconverter.utils.ItemClickCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DefaultCurrencySelectorDialog : BottomSheetDialogFragment(), ItemClickCallback<Currency> {
    private lateinit var mBinding: FragmentDefaultCurrencySelectorBinding
    private lateinit var defaultCurrencyAdapter: DefaultCurrencyAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDefaultCurrencySelectorBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupObserver()
    }

    private fun setupRecycler() {
        defaultCurrencyAdapter =
            DefaultCurrencyAdapter(mainViewModel.selectedBase.value ?: "USD", this)
        mBinding.apply {
            mBinding.recyclerDefaultCurrencySelector.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = defaultCurrencyAdapter
            }
        }
    }

    private fun setupObserver() {
        mainViewModel.currencyExchangeLiveData.observe(viewLifecycleOwner) {
            if (this::defaultCurrencyAdapter.isInitialized) {
                defaultCurrencyAdapter.submitList(it)
            }
        }
    }

    companion object {
        const val SELECTED_CURRENCY = "selected_currency"
        const val TAG = "default_currency_dialog"
        fun newInstance(selected: String?): DefaultCurrencySelectorDialog {
            val fragment = DefaultCurrencySelectorDialog()
            fragment.arguments = Bundle().apply {
                putString(SELECTED_CURRENCY, selected)
            }
            return fragment
        }
    }

    override fun onActionClick(data: Currency, position: Int?) {
        mainViewModel.selectedBase.value = data.name
        mainViewModel.selectedBaseValueAgainstUSD = data.valueInUsd ?: 1.0
        mainViewModel.handleExchangeRateData()
        dismiss()
    }
}
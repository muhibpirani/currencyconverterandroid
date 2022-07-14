package com.sample.currencyconverter.ui.currencyview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.currencyconverter.databinding.FragmentCurrencyViewBinding
import com.sample.currencyconverter.ui.defaultcurrencydialog.DefaultCurrencySelectorDialog
import com.sample.currencyconverter.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyViewFragment : Fragment() {
    private lateinit var mBinding: FragmentCurrencyViewBinding
    private lateinit var currencyViewAdapter: CurrencyViewAdapter
    private val mainViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCurrencyViewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupRecyclerView()
        setupClickListeners()
        setupObserver()
    }

    private fun setupClickListeners() {
        mBinding.apply {
            txtDefaultCurrency.setOnClickListener {
                openDefaultCurrencySelectorDailog()
            }
        }
    }

    private fun openDefaultCurrencySelectorDailog() {
        val fragment = DefaultCurrencySelectorDialog.newInstance(mainViewModel.selectedBase.value)
        fragment.show(childFragmentManager, DefaultCurrencySelectorDialog.TAG)
    }

    private fun setupView() {
        mBinding.mainViewModel = mainViewModel
        mBinding.lifecycleOwner = this

        mBinding.edtAmount.doAfterTextChanged {
            it?.let {
                if (it.toString().isEmpty().not()) {
                    mainViewModel.multiplyAmount(it.toString())
                } else {
                    mainViewModel.multiplyAmount("1.0")
                }
            }
        }
    }

    private fun setupRecyclerView() {
        currencyViewAdapter = CurrencyViewAdapter()
        mBinding.recyclerCurrencies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = currencyViewAdapter
        }
    }

    private fun setupObserver() {
        mainViewModel.currencyExchangeLiveData.observe(viewLifecycleOwner) {
            if (this::currencyViewAdapter.isInitialized) {
                currencyViewAdapter.submitList(null)
                currencyViewAdapter.submitList(it)
            }
        }
    }
}
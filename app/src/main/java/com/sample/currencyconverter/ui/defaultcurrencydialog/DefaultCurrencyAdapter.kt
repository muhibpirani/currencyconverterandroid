package com.sample.currencyconverter.ui.defaultcurrencydialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sample.currencyconverter.databinding.ItemDefaultCurrencyBinding
import com.sample.currencyconverter.model.Currency
import com.sample.currencyconverter.ui.currencyview.CurrencyDiffComparator
import com.sample.currencyconverter.utils.ItemClickCallback

class DefaultCurrencyAdapter(
    var selectedCurrency: String,
    private val itemClickCallback: ItemClickCallback<Currency>
) :
    ListAdapter<Currency, RecyclerView.ViewHolder>(CurrencyDiffComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CurrencyHolder(
            ItemDefaultCurrencyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position).apply {
            when (holder) {
                is CurrencyHolder -> {
                    holder.setData(this)
                    holder.itemDefault.root.setOnClickListener {
                        selectedCurrency = this.name ?: "USD"
                        notifyDataSetChanged()
                        itemClickCallback.onActionClick(this, position)
                    }
                }
            }
        }
    }

    inner class CurrencyHolder(val itemDefault: ItemDefaultCurrencyBinding) :
        RecyclerView.ViewHolder(itemDefault.root) {
        fun setData(currency: Currency) {
            if (selectedCurrency.lowercase().equals(currency.name?.lowercase())) {
                itemDefault.imgCurrencySelected.visibility = View.VISIBLE
            } else {
                itemDefault.imgCurrencySelected.visibility = View.GONE
            }
            itemDefault.currency = currency
            itemDefault.executePendingBindings()
        }
    }
}
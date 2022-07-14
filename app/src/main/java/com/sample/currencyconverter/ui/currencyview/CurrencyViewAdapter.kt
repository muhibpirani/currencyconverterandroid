package com.sample.currencyconverter.ui.currencyview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sample.currencyconverter.databinding.ItemCurrencyBinding
import com.sample.currencyconverter.model.Currency

class CurrencyViewAdapter() :
    ListAdapter<Currency, RecyclerView.ViewHolder>(CurrencyDiffComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CurrencyHolder(
            ItemCurrencyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position).apply {
            when (holder) {
                is CurrencyHolder -> holder.setData(this)
            }
        }
    }

    inner class CurrencyHolder(val itemCurrencyBinding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(itemCurrencyBinding.root) {
        fun setData(currency: Currency) {
            itemCurrencyBinding.currency = currency
            itemCurrencyBinding.executePendingBindings()
        }
    }
}

object CurrencyDiffComparator : DiffUtil.ItemCallback<Currency>() {
    override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
        return oldItem.name == newItem.name && oldItem.valueAgainstSelected == newItem.valueAgainstSelected
                && oldItem.valueAfterMultipledValue == newItem.valueAfterMultipledValue
    }

    override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
        return oldItem == newItem && oldItem.valueAfterMultipledValue == newItem.valueAfterMultipledValue
    }
}
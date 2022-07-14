package com.sample.currencyconverter.utils

interface ItemClickCallback<T> {
    fun onActionClick(data: T, position: Int?)
}
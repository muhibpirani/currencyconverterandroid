package com.sample.currencyconverter.model

data class Currency(
    var name: String? = null,
    var valueAgainstSelected: Double? = null,
    var valueInUsd : Double? = null,
    var valueAfterMultipledValue : Double? = null
)
package com.test.app.ui.list

class RatesItemViewModel(
    private val onClickRatesItemObservable: OnClickRatesItemObservable,
    val rateTextWatcher: RateTextWatcher
) {

    fun onClickItemRate(ratesItem: RatesItem) {
        onClickRatesItemObservable.emitItem(ratesItem)
    }
}
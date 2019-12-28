package com.test.app.ui.list

class RatesItemViewModel(
    private val onClickRatesItemObservable: OnClickRatesItemObservable,
    val rateTextWatcher: RateTextWatcher,
    val onEditRateClickListener: OnEditRateClickListener
) {

    fun onClickItemRate(ratesItem: RatesItem) {
        onClickRatesItemObservable.emitItem(ratesItem)
    }
}
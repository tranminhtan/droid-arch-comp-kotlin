package com.task.app.ui.list

import com.task.app.ui.support.OnClickRatesItemObservable
import com.task.app.ui.support.OnEditRateClickListener

class RatesItemViewModel(
    private val onClickRatesItemObservable: OnClickRatesItemObservable,
    val rateTextWatcher: RateTextWatcher,
    val onEditRateClickListener: OnEditRateClickListener
) {

    fun onClickItemRate(ratesItem: RatesItem) {
        onClickRatesItemObservable.emitItem(ratesItem)
    }
}
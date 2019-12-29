package com.task.app.ui.list

import com.task.app.ui.utils.OnClickRatesItemObservable
import com.task.app.ui.utils.OnEditRateClickListener

class RatesItemViewModel(
    private val onClickRatesItemObservable: OnClickRatesItemObservable,
    val rateTextWatcher: RateTextWatcher,
    val onEditRateClickListener: OnEditRateClickListener
) {

    fun onClickItemRate(ratesItem: RatesItem) {
        onClickRatesItemObservable.emitItem(ratesItem)
    }
}
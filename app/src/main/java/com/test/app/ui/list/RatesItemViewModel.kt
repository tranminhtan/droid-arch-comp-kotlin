package com.test.app.ui.list

import com.test.app.ui.utils.OnClickRatesItemObservable
import com.test.app.ui.utils.OnEditRateClickListener

class RatesItemViewModel(
    private val onClickRatesItemObservable: OnClickRatesItemObservable,
    val rateTextWatcher: RateTextWatcher,
    val onEditRateClickListener: OnEditRateClickListener
) {

    fun onClickItemRate(ratesItem: RatesItem) {
        onClickRatesItemObservable.emitItem(ratesItem)
    }
}
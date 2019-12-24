package com.test.app.ui.list

class RatesItemViewModel(private val onClickRatesItemStream: OnClickRatesItemStream) {

    fun onClickItemRate(ratesItem: RatesItem) {
        onClickRatesItemStream.emitItem(ratesItem)
    }
}
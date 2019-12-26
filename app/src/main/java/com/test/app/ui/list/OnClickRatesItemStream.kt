package com.test.app.ui.list

import com.test.app.service.CurrencyRateRepository
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject


class OnClickRatesItemStream {

    private val subject: Subject<RatesItem> = BehaviorSubject.createDefault(CurrencyRateRepository.BASE_RATES_ITEM)

    fun observeClickItem(): Observable<RatesItem> = subject

    fun emitItem(item: RatesItem) {
        subject.onNext(item)
    }
}
package com.test.app.ui.list

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject


class OnClickRatesItemStream {
    companion object {
        private const val BASE_CODE = "EUR"
        private const val BASE_AMOUNT = "1.0000"
    }

    private val subject: Subject<RatesItem> = BehaviorSubject.createDefault(RatesItem(BASE_CODE, "", BASE_AMOUNT, 0))

    fun observeClickItem(): Observable<RatesItem> = subject

    fun emitItem(item: RatesItem) {
        subject.onNext(item)
    }
}
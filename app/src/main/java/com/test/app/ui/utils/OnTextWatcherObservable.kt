package com.test.app.ui.utils

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit


class OnTextWatcherObservable {

    private val subject: Subject<String> = PublishSubject.create()

    fun observeRateChange(): Observable<String> =
        subject.distinctUntilChanged()
            .debounce(100, TimeUnit.MILLISECONDS, Schedulers.computation())

    fun emitRate(rate: String) {
        subject.onNext(rate)
    }
}
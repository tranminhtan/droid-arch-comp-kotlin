package com.task.app.ui.utils

import com.task.app.base.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit


class OnTextWatcherObservable(private val schedulersProvider: SchedulersProvider) {

    private val subject: Subject<String> = PublishSubject.create()

    fun observeRateChange(): Observable<String> =
        subject.distinctUntilChanged()
            .debounce(100, TimeUnit.MILLISECONDS, schedulersProvider.computation())

    fun emitRate(rate: String) {
        subject.onNext(rate)
    }
}
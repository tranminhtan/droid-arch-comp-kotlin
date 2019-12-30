package com.task.app.base

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer

@Suppress("unused")
class RxTransformer<T, R>(private val commands: Commands<T, R>) {

    interface Commands<T, R> {
        fun applyObs(upstream: Observable<T>): ObservableSource<R>
        fun applySingle(upstream: Single<T>): SingleSource<R>
    }

    fun forObservable(): ObservableTransformer<T, R> {
        return ObservableTransformer { upstream -> commands.applyObs(upstream) }
    }

    fun forSingle(): SingleTransformer<T, R> {
        return SingleTransformer { upstream -> commands.applySingle(upstream) }
    }
}
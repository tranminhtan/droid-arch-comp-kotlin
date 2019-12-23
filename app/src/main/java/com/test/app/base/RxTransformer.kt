package com.test.app.base

import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.CompletableTransformer
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.Maybe
import io.reactivex.MaybeSource
import io.reactivex.MaybeTransformer
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import org.reactivestreams.Publisher

@Suppress("unused")
class RxTransformer<T, R>(private val commands: Commands<T, R>) {

    interface Commands<T, R> {
        fun applyObs(upstream: Observable<T>): ObservableSource<R>
        fun applyCompletable(upstream: Completable): CompletableSource
        fun applyPublisher(upstream: Flowable<T>): Publisher<R>
        fun applySingle(upstream: Single<T>): SingleSource<R>
        fun applyMaybe(upstream: Maybe<T>): MaybeSource<R>
    }

    fun forObservable(): ObservableTransformer<T, R> {
        return ObservableTransformer { upstream -> commands.applyObs(upstream) }
    }

    fun forCompletable(): CompletableTransformer {
        return CompletableTransformer { upstream -> commands.applyCompletable(upstream) }
    }

    fun forFlowable(): FlowableTransformer<T, R> {
        return FlowableTransformer { upstream -> commands.applyPublisher(upstream) }
    }

    fun forSingle(): SingleTransformer<T, R> {
        return SingleTransformer { upstream -> commands.applySingle(upstream) }
    }

    fun forMaybe(): MaybeTransformer<T, R> {
        return MaybeTransformer { upstream -> commands.applyMaybe(upstream) }
    }
}
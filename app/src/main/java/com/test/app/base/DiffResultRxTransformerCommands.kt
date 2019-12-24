package com.test.app.base

import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import androidx.recyclerview.widget.DiffUtil
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.MaybeSource
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher

internal class DiffResultRxTransformerCommands<T>(private val callback: Callback<T>) :
    RxTransformer.Commands<List<T>, List<T>> {

    internal interface Callback<T> {
        fun getList(): List<T>

        @UiThread
        fun setList(list: List<T>?)

        @UiThread
        fun setList(
            diffResult: DiffUtil.DiffResult,
            list: List<T>?
        )

        fun areItemsTheSame(oldItem: T, newItem: T): Boolean

        fun areContentsTheSame(oldItem: T, newItem: T): Boolean

        fun getChangePayload(oldItem: T, newItem: T): Any?
    }

    override fun applyObs(upstream: Observable<List<T>>): ObservableSource<List<T>> {
        return upstream
            .distinctUntilChanged()
            .observeOn(Schedulers.computation())
            .concatMap { newList ->
                calculateDiffResult(
                    callback.getList(),
                    newList
                ).toObservable()
            }
    }

    override fun applyCompletable(upstream: Completable): CompletableSource {
        return Completable.error(UnsupportedOperationException("Completable not supported"))
    }

    override fun applyPublisher(upstream: Flowable<List<T>>): Publisher<List<T>> {
        return upstream
            .distinctUntilChanged()
            .observeOn(Schedulers.computation())
            .concatMap { newList -> calculateDiffResult(callback.getList(), newList).toFlowable() }
    }

    override fun applySingle(upstream: Single<List<T>>): SingleSource<List<T>> {
        return upstream
            .observeOn(Schedulers.computation())
            .flatMap { newList -> calculateDiffResult(callback.getList(), newList) }
    }

    override fun applyMaybe(upstream: Maybe<List<T>>): MaybeSource<List<T>> {
        return upstream
            .observeOn(Schedulers.computation())
            .flatMap { newList -> calculateDiffResult(callback.getList(), newList).toMaybe() }
    }

    /**
     * @param oldList - list from DataBindingRecyclerViewAdapter
     * @param newList - The new list to compare to
     * @return DiffUtil.DiffResult
     * @see DiffUtil.calculateDiff
     */
    @VisibleForTesting
    @WorkerThread
    fun calculateDiff(
        oldList: List<T>,
        newList: List<T>
    ): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean {
                return callback.areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])
            }

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean {
                return callback.areContentsTheSame(
                    oldList[oldItemPosition],
                    newList[newItemPosition]
                )
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                return callback.getChangePayload(oldList[oldItemPosition], newList[newItemPosition])
                    ?: super.getChangePayload(oldItemPosition, newItemPosition)
            }
        })
    }

    private fun calculateDiffResult(
        oldList: List<T>,
        newList: List<T>
    ): Single<List<T>> {
        val diffResult =
            (if (oldList.isNotEmpty()) calculateDiff(oldList, newList) else null) ?: return Single
                .just(newList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEvent { newList1, _ -> callback.setList(newList1) }
                .observeOn(Schedulers.computation())

        return Single
            .just(diffResult)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnEvent { diffResult12, _ -> callback.setList(diffResult12, newList) }
            .observeOn(Schedulers.computation())
            .map { newList }
    }
}
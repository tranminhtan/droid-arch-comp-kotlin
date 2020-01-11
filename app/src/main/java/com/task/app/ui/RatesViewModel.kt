package com.task.app.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.task.app.base.SchedulersProvider
import com.task.app.service.CurrencyRateRepository.Companion.EMPTY_RATES_ITEM
import com.task.app.ui.list.RatesAdapter
import com.task.app.ui.list.RatesItem
import com.task.app.ui.support.OnClickRatesItemObservable
import com.task.app.ui.support.OnTextWatcherObservable
import com.task.app.ui.support.isEqual
import com.task.app.ui.support.toBigDecimalOrZero
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.math.BigDecimal
import java.util.Collections
import java.util.concurrent.TimeUnit

private const val DELAY_IN_SEC = 1L

class RatesViewModel(
    private val schedulersProvider: SchedulersProvider,
    placeHolderRatesItemUseCase: GetPlaceHolderRatesItemUseCase,
    private val serverRatesItemUseCase: GetServerRatesItemUseCase,
    private val onClickRatesItemObservable: OnClickRatesItemObservable,
    private val onTextWatcherObservable: OnTextWatcherObservable,
    val adapter: RatesAdapter
) : LifecycleObserver {

    init {
        adapter.submitList(placeHolderRatesItemUseCase.getPlaceHolderRates())
    }

    private lateinit var disposable: Disposable

    // A valve to turn on/off updating currency rate
    private val valveSubject = BehaviorSubject.create<RatesItem>()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun observeRxStreams() {
        disposable =
            Observable.merge(
                observeOnItemClick(),
                observeGetCurrencyRatesInterval(),
                observeRateTextChange()
            )
                .subscribe(Functions.emptyConsumer(), Consumer { Timber.e(it) })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disposeRxStreams() {
        disposable.dispose()
    }

    private fun observeRateTextChange(): Observable<List<RatesItem>> {
        return onTextWatcherObservable.observeRateChange()
            .doOnNext { Timber.w("Text changed %s", it) }
            .doOnNext { emitRatesItem(EMPTY_RATES_ITEM) } // Stop spamming server
            .subscribeOn(schedulersProvider.computation())
            .switchMapSingle { newBase: String ->
                val baseItem = adapter.currentList[0] // First item is base item

                // Call api if the current base is 0
                if (baseItem.rate.toBigDecimalOrZero().isEqual(BigDecimal.ZERO)
                    && newBase.toBigDecimalOrZero().isEqual(BigDecimal.ZERO)
                ) {
                    Single.just(emptyList<RatesItem>())
                        .doOnSuccess { emitRatesItem(baseItem.copy(rate = newBase)) }
                }
                // Calculate rates locally
                else {
                    Observable.fromIterable(adapter.currentList)
                        .map { currItem: RatesItem ->
                            val newRate = serverRatesItemUseCase.calculateNewRate(newBase, baseItem.rate, currItem.rate)
                            currItem.copy(rate = newRate)
                        }
                        .collectInto(arrayListOf<RatesItem>(), { list, item -> list.add(item) })
                        .map {
                            it[0] = it[0].copy(rate = newBase)
                            it
                        }
                        .compose(adapter)
                        .delay(DELAY_IN_SEC, TimeUnit.SECONDS, schedulersProvider.computation())
                        .doOnSuccess { emitRatesItem(baseItem.copy(rate = newBase)) }
                }
            }
    }

    fun observeOnItemClick(): Observable<Any> {
        return onClickRatesItemObservable.observeClickItem()
            .doOnNext { emitRatesItem(EMPTY_RATES_ITEM) } // Stop spamming server
            .observeOn(schedulersProvider.ui())
            .switchMapSingle { item: RatesItem ->
                adapter.moveSelectedItemToTop(item)
                    .flatMap {
                        if (it.isNotEmpty()) {
                            Single.just(it).compose(adapter)
                        } else {
                            Single.just(it)
                        }
                    }
                    .doOnSuccess { emitRatesItem(item) }
            }
    }

    private fun observeGetCurrencyRatesInterval(): Observable<List<RatesItem>> {
        return valveSubject
            .distinctUntilChanged()
            .subscribeOn(schedulersProvider.computation())
            .switchMap { item: RatesItem ->
                Observable.just(item)
                    .takeWhile { it != EMPTY_RATES_ITEM } // Not stop spamming server signal
                    .switchMapSingle { validItem: RatesItem ->
                        serverRatesItemUseCase.getCurrencyRates(validItem.code, validItem.rate)
                            .onErrorReturnItem(Collections.emptyList()) // Simply swallow error
                    }
                    .filter { it.isNotEmpty() }
                    .compose(adapter)
                    .delay(DELAY_IN_SEC, TimeUnit.SECONDS, schedulersProvider.computation())
                    .repeat()
            }
    }

    private fun emitRatesItem(item: RatesItem) {
        valveSubject.onNext(item)
    }
}
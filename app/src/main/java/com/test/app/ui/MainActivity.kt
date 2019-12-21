package com.test.app.ui

import android.os.Bundle
import android.util.Log
import com.test.app.R
import com.test.app.service.CurrencyRateService
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var retrofit: Retrofit

    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        disposable = Observable.interval(1, TimeUnit.SECONDS, Schedulers.computation())
            .map { it.toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { title = it }, Functions.emptyConsumer())

//        disposable = viewModel.getCurrencyRates("EUR")
//            .map { it.rates }
//            .subscribe({ Log.v("Test", it.toString()) }, { Log.v("Test", it.message!!) })
        val service = retrofit.create(CurrencyRateService::class.java)
        service.getCurrencyRates("EUR")
            .doOnSuccess { Log.v("Test", it.toString()) }
            .doOnError(Consumer { Log.v("Test", it.message) })
            .subscribe()

    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}

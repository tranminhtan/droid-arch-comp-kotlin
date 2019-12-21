package com.test.app.ui

import android.os.Bundle
import com.test.app.R
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import timber.log.Timber
import java.util.*
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
                .switchMapSingle {
                    viewModel.getCurrencyRates("EUR")
                            .map { it.rates }
                            .onErrorReturnItem(Collections.emptyMap())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("Test ${it.size}")
                    title = it.size.toString()
                }, { Timber.e(it) })
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}

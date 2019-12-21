package com.test.app.ui

import android.os.Bundle
import com.test.app.R
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RatesActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModel: RatesViewModel

    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_name)

        disposable = Observable.interval(1, TimeUnit.SECONDS, Schedulers.computation())
            .switchMapSingle {
                viewModel.getCurrencyRates("EUR")
                    .map { it.rates }
                    .onErrorReturnItem(Collections.emptyMap())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("Test ${it.size}")
            }, { Timber.e(it) })
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}

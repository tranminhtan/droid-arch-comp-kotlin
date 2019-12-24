package com.test.app.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.test.app.BR
import com.test.app.R
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import timber.log.Timber
import javax.inject.Inject

class RatesActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModel: RatesViewModel
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.setVariable(BR.vm, viewModel)
        title = getString(R.string.app_name)
    }

    override fun onResume() {
        super.onResume()
        disposable = viewModel.updateCurrencyRatesInterval()
            .subscribe(Functions.emptyConsumer(), Consumer { Timber.e(it) })
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }
}

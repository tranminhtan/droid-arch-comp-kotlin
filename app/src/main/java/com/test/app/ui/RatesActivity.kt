package com.test.app.ui

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.test.app.BR
import com.test.app.R
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import kotlinx.android.synthetic.main.activity_main.view.recyclerView
import timber.log.Timber
import javax.inject.Inject

class RatesActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModel: RatesViewModel

    @Inject
    lateinit var adapter: RatesListAdapter

    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // https://issuetracker.google.com/issues/37018279
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding.root.recyclerView.scrollToPosition(0)
            }
        })
        binding.setVariable(BR.vm, viewModel)
        title = getString(R.string.app_name)

        binding.root.recyclerView.setOnTouchListener { v, _ ->
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
    }

    override fun onResume() {
        super.onResume()
        disposable =
            Observable.merge(
                viewModel.observeOnItemClick(),
                viewModel.observeGetCurrencyRatesInterval(),
                viewModel.observeRateTextChange()
            )
                .subscribe(Functions.emptyConsumer(), Consumer { Timber.e(it) })
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }
}

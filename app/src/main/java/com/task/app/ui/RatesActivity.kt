package com.task.app.ui

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.task.app.BR
import com.task.app.R
import com.task.app.base.LifecycleDaggerActivity
import com.task.app.ui.list.RatesAdapter
import kotlinx.android.synthetic.main.activity_main.view.recyclerView
import javax.inject.Inject

class RatesActivity : LifecycleDaggerActivity() {

    @Inject
    lateinit var viewModel: RatesViewModel

    @Inject
    lateinit var adapter: RatesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        title = getString(R.string.app_name)

        // https://issuetracker.google.com/issues/37018279
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding.root.recyclerView.scrollToPosition(0)
            }
        })

        lifecycle.addObserver(viewModel)
        binding.setVariable(BR.vm, viewModel)
        binding.root.recyclerView.setOnTouchListener { v, _ ->
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
    }
}

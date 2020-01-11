package com.task.app.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.task.app.base.RxTransformer
import com.task.app.databinding.ItemCurrencyRateBinding
import com.task.app.service.CurrencyRateRepository
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.SingleSource

class RatesAdapter(private val viewModel: RatesItemViewModel) : ListAdapter<RatesItem, RatesAdapter.RatesViewModel>(RatesDiffUtils()),
    RxTransformer<List<RatesItem>, List<RatesItem>> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewModel {
        return RatesViewModel.from(parent)
    }

    override fun onBindViewHolder(holder: RatesViewModel, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }

    override fun apply(upstream: Observable<List<RatesItem>>): ObservableSource<List<RatesItem>> {
        return upstream.doOnNext { submitList(it) }
    }

    override fun apply(upstream: Single<List<RatesItem>>): SingleSource<List<RatesItem>> {
        return upstream.doOnSuccess { submitList(it) }
    }

    @UiThread
    fun moveSelectedItemToTop(item: RatesItem): Single<List<RatesItem>> {
        return Single.fromCallable { moveSelectedItemToTop(currentList, item) }
    }

    private fun moveSelectedItemToTop(list: List<RatesItem>, item: RatesItem): List<RatesItem> {
        val newList = list.toMutableList()
        if (newList.isNotEmpty() && item != CurrencyRateRepository.BASE_RATES_ITEM) {
            val currentPos = newList.indexOf(item)

            return if (currentPos > 0) {
                newList.remove(item).also {
                    newList.add(0, item)
                }
                submitList(newList)
                notifyItemMoved(currentPos, 0)

                // Update editable state
                newList[0] = newList[0].copy(editable = true)
                newList[1] = newList[1].copy(editable = false)
                newList
            } else {
                emptyList()
            }
        }
        return emptyList()
    }

    class RatesViewModel(private val binding: ItemCurrencyRateBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: RatesItemViewModel, item: RatesItem) {
            binding.itemVm = viewModel
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RatesViewModel {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCurrencyRateBinding.inflate(layoutInflater, parent, false)

                return RatesViewModel(binding)
            }
        }
    }
}
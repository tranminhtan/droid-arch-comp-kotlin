package com.test.app.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class DataBindingRecyclerViewAdapter<T> @JvmOverloads constructor(
    context: Context? = null,
    rxTransformer: RxTransformer<List<T>, List<T>>? = null
) : DiffResultRecyclerViewAdapter<T, RecyclingViewHolder>(rxTransformer) {

    private var layoutInflater: LayoutInflater? =
        if (context == null) null else LayoutInflater.from(context)

    protected open fun setData(
        position: Int,
        binder: ViewDataBinding,
        view: View,
        viewType: Int,
        data: T
    ) {
        setData(binder, view, viewType, data)
    }

    protected open fun setData(
        binder: ViewDataBinding,
        view: View,
        viewType: Int,
        data: T
    ) {
    }

    protected open fun getViewHolder(
        binder: ViewDataBinding,
        viewType: Int
    ): DataBindingViewHolder<out T> {
        return getViewHolder(binder)
    }

    protected open fun getViewHolder(binder: ViewDataBinding): DataBindingViewHolder<out T> {
        return DataBindingViewHolder(binder)
    }

    @LayoutRes
    protected abstract fun getLayoutIdForViewType(viewType: Int): Int

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclingViewHolder {
        layoutInflater =
            layoutInflater ?: LayoutInflater.from(parent.context)
                    ?: throw IllegalStateException("Missing LayoutInflater")

        val binder = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater!!,
            getLayoutIdForViewType(viewType),
            parent,
            false
        )
        return getViewHolder(binder, viewType)
    }

    override fun onBindViewHolder(
        holder: RecyclingViewHolder,
        position: Int
    ) {
        if (holder is DataBindingViewHolder<*>) {
            try {

                @Suppress("UNCHECKED_CAST") val viewHolder = holder as DataBindingViewHolder<T>
                val item = getItemAtPosition(position)
                setData(
                    position,
                    viewHolder.binder,
                    holder.itemView,
                    getItemViewType(position),
                    item
                )
                viewHolder.setData(item)
                viewHolder
                    .binder
                    .executePendingBindings()
            } catch (e: ClassCastException) {
                // Not doing anything
            }
        }
    }
}
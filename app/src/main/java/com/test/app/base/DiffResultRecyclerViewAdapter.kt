package com.test.app.base

import androidx.annotation.UiThread
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * A RecyclerView.Adapter that works with background [androidx.recyclerview.widget.DiffUtil.DiffResult] calculation.
 *
 * Provide [RxTransformer] that works with Reactive operators.
 */
abstract class DiffResultRecyclerViewAdapter<T1, T2 : RecyclerView.ViewHolder> @JvmOverloads protected constructor(
    rxTransformer: RxTransformer<List<T1>, List<T1>>? = null
) :
    RecyclerView.Adapter<T2>(),
    DiffResultRxTransformerCommands.Callback<T1> {

    private val rxTransformer: RxTransformer<List<T1>, List<T1>> = rxTransformer ?: RxTransformer(
        DiffResultRxTransformerCommands(this)
    )

    private var list: List<T1> = emptyList()

    override fun getList(): List<T1> {
        return list
    }

    /**
     * Swap the underneath list for this adapter and trigger an immediate [RecyclerView.Adapter.notifyDataSetChanged]
     * <p>
     * Perform zero action if the {@param list} is null.
     *
     * @param list - The new list to swap to
     */
    @UiThread
    override fun setList(list: List<T1>?) {
        if (list == null) {
            return
        }

        this.list = list.toList()
        notifyDataSetChanged()
    }

    /**
     * Swap the underneath list for this adapter and call
     * [androidx.recyclerview.widget.DiffUtil.DiffResult.dispatchUpdatesTo]
     *
     *
     * Perform zero action if the {@param list} is null.
     *
     *
     * Throw NullPointerException if {@param diffResult} is null.
     *
     * @param diffResult - The DiffResult calculated
     * @param list       - The new list to swap to
     */
    @UiThread
    override fun setList(
        diffResult: DiffUtil.DiffResult,
        list: List<T1>?
    ) {
        if (list == null) {
            return
        }

        this.list = list.toList()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return getList().size
    }

    override fun onViewRecycled(holder: T2) {
        if (holder is RecyclingViewHolder) {
            holder.onViewRecycled()
        }
        super.onViewRecycled(holder)
    }

    /**
     * Return the item at position {@param position}
     *
     * @param position int
     * @return T
     */
    open fun getItemAtPosition(position: Int): T1 {
        return getList()[position]
    }

    /**
     * Create a new [RxTransformer] that perform background DiffResult calculation and dispatch to adapter.
     * This transformer emits on Schedulers.computation().
     *
     * @return Transformer
     */
    fun asRxTransformer(): RxTransformer<List<T1>, List<T1>> {
        return rxTransformer
    }
}
package com.task.app.binding

import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.task.app.ui.list.RateTextWatcher
import com.task.app.ui.list.RatesItem

@BindingAdapter("visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter(value = ["addRateTextChangedListener", "ratesItem"], requireAll = true)
fun addTextChangedListener(editText: EditText, rateTextWatcher: RateTextWatcher, ratesItem: RatesItem) {
    if (ratesItem.editable) {
        editText.removeTextChangedListener(rateTextWatcher)
        rateTextWatcher.editableText = editText.editableText

        editText.addTextChangedListener(rateTextWatcher)
        editText.setSelection(editText.text.length)
    } else {
        editText.removeTextChangedListener(rateTextWatcher)
    }
}
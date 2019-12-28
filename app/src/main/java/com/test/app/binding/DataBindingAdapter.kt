package com.test.app.binding

import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.test.app.ui.list.RateTextWatcher

@BindingAdapter("visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter(value = ["addRateTextChangedListener", "editableRate"], requireAll = true)
fun addTextChangedListener(editText: EditText, rateTextWatcher: RateTextWatcher, editable: Boolean) {
    if (editable) {
        editText.addTextChangedListener(rateTextWatcher)
    } else {
        editText.removeTextChangedListener(rateTextWatcher)
    }
}
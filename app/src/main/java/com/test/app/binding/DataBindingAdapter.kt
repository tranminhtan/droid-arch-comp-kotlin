package com.test.app.binding

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}
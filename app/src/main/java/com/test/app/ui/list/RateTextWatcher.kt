package com.test.app.ui.list

import android.text.Editable
import android.text.TextWatcher

class RateTextWatcher(
    private val onTextWatcherObservable: OnTextWatcherObservable
) : TextWatcher {

    var editableText: Editable? = null

    override fun afterTextChanged(s: Editable?) {
        if (editableText === s) {
            onTextWatcherObservable.emitRate(s.toString().trim())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // No-opt
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // No-opt
    }
}
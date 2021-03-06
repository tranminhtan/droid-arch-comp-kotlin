package com.task.app.ui.support

import android.view.View
import android.widget.EditText

class OnEditRateClickListener : View.OnClickListener {

    override fun onClick(v: View?) {
        (v as EditText).setSelection(v.text.length) // To keep EditText cursor on the right
    }
}
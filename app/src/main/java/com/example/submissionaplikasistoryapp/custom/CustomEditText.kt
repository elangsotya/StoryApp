package com.example.submissionaplikasistoryapp.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class CustomEditText: AppCompatEditText, View.OnTouchListener {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s?.length ?: 0) < 8) {
                    error = "Password minimal 8 karakter"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}
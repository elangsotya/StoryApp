package com.example.submissionaplikasistoryapp.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.submissionaplikasistoryapp.R

class CustomButton: AppCompatButton {

    private lateinit var enabledBackground: Drawable
    private lateinit var disableBackground: Drawable
    private var txtColor: Int = 0

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    // Metode onDraw() digunakan untuk mengcustom button ketika enable dan disable
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Mengubah background dari Button
        background = if(isEnabled) enabledBackground else disableBackground
        // Mengubah warna text pada button
        setTextColor(txtColor)
        // Mengubah ukuran text pada button
        textSize = 12f
        // Menjadikan object pada button menjadi center
        gravity = Gravity.CENTER
        // Mengubah text pada button pada kondisi enable dan disable
        text = if(isEnabled) "Submit" else "Password Minimal 8 Karakter"
    }

    private fun init(){
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disableBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }
}
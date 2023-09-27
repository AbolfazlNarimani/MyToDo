package com.abe.todolist.customViews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class TDEditText (context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs) {

    init {
        applyFont()
    }


    private fun applyFont() {

        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
        setTypeface(typeface)
    }
}
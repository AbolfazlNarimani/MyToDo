package com.example.todolist.customViews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView

class TDMultiLine  (context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs){
    init{
        applyFont()
    }

    private fun applyFont() {

        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "robotoregular.ttf")
        setTypeface(typeface)
    }
}
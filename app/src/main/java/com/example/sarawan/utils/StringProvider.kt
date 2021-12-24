package com.example.sarawan.utils

import android.content.Context
import androidx.annotation.StringRes

class StringProvider(private val context: Context) {

    fun getString(@StringRes stringRes: Int): String {
        return context.getString(stringRes)
    }
}
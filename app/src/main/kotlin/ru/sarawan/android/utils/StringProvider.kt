package ru.sarawan.android.utils

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject

class StringProvider @Inject constructor(private val context: Context) {

    fun getString(@StringRes stringRes: Int): String {
        return context.getString(stringRes)
    }
}
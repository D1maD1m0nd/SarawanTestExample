package ru.sarawan.android.utils.constants

import androidx.annotation.StringRes
import ru.sarawan.android.R

enum class SortBy(val id: Int, @StringRes val text: Int, @StringRes val description: Int) {
    PRICE_ASC(0, R.string.sort_price, R.string.min_price),
    PRICE_DES(1, R.string.sort_price, R.string.min_price),
    ALPHABET(2, R.string.sort_alphabet, R.string.min_price),
    DISCOUNT(3, R.string.sort_discount, R.string.max_discount)
}
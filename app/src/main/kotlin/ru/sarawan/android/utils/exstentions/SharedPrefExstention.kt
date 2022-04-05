package ru.sarawan.android.utils.exstentions

import android.content.SharedPreferences

var SharedPreferences.token: String?
    get() = this.getString("token", "4c2cb1e36b9e50cbd742a9e57a89d8e24c3f8eeb")
    set(value) {
        this.edit()
            .putString("token", value)
            .apply()
    }

var SharedPreferences.userId: Long?
    get() = this.getLong("userId", 12)
    set(value) {
        if (value != null) {
            this.edit()
                .putLong("userId", value)
                .apply()
        }
    }

var SharedPreferences.basketId: Int?
    get() = this.getInt("basketId", UNREGISTERED.toInt())
    set(value) {
        if (value != null) {
            this.edit()
                .putInt("basketId", value)
                .apply()
        }
    }

const val UNREGISTERED = 1L


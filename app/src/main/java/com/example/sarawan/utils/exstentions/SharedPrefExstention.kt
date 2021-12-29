package com.example.sarawan.utils.exstentions

import android.content.SharedPreferences

var SharedPreferences.token: String?
    get() = this.getString("token", null)
    set(value) {
        this.edit()
            .putString("token", value)
            .apply()
    }

var SharedPreferences.userId: Long?
    get() = this.getLong("userId", UNREGISTERED)
    set(value) {
        if (value != null) {
            this.edit()
                .putLong("userId", value)
                .apply()
        }
    }

var SharedPreferences.basketId:Int?
    get() = this.getInt("basketId", UNREGISTERED.toInt())
    set(value) {
        if (value != null) {
            this.edit()
                .putInt("basketId", value)
                .apply()
        }
    }

const val UNREGISTERED = 1L


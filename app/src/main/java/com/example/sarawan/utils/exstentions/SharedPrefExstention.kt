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
    get() = this.getLong("userId", -1)
    set(value) {
        if (value != null) {
            this.edit()
                .putLong("userId", value)
                .apply()
        }
    }

var SharedPreferences.basketId:Int?
    get() = this.getInt("basketId", -1)
    set(value) {
        if (value != null) {
            this.edit()
                .putInt("basketId", value)
                .apply()
        }
    }
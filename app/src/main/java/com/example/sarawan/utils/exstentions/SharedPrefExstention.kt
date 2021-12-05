package com.example.sarawan.utils.exstentions

import android.content.SharedPreferences

var SharedPreferences.token: String?
    get() = this.getString("token", null)
    set(value) {
        this.edit()
            .putString("token", value)
            .apply()
    }

var SharedPreferences.userId: Int?
    get() = this.getInt("userId", -1)
    set(value) {
        if (value != null) {
            this.edit()
                .putInt("token", value)
                .apply()
        }
    }
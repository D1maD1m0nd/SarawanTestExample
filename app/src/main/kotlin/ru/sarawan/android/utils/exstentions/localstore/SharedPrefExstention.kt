package ru.sarawan.android.utils.exstentions.localstore

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPrefStore @Inject constructor(
    private val sharedPreferences: SharedPreferences): LocalStore {
    override var token : String?
        get() = sharedPreferences.getString("token", "")
        set(value) {
            sharedPreferences.edit()
                .putString("token", value)
                .apply()
        }
    override var userId: Long?
        get() = sharedPreferences.getLong("userId", UNREGISTERED)
        set(value) {
            if (value != null) {
                sharedPreferences.edit()
                    .putLong("userId", value)
                    .apply()
            }
        }
    companion object{
        const val UNREGISTERED = 1L
    }
}

var SharedPreferences.token: String?
    get() = this.getString("token", "")
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


package ru.sarawan.android.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import ru.sarawan.android.playground.Information

class MoshiCustomAdapter(val moshi: Moshi) {
    private val jsonAdapter: JsonAdapter<Information> = moshi.adapter(Information ::class.java)


    fun infoFromJson(json : String) : Information? {
        return jsonAdapter.fromJson(json)
    }
}
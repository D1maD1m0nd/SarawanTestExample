package ru.sarawan.android.utils.MoshiAdapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import ru.sarawan.android.model.data.address.yandexMap.KindType

class EnumKindAdapter {
    @FromJson
    fun fromJson(jsonReader: JsonReader, delegate: JsonAdapter<KindType>): KindType? {
        val value = jsonReader.nextString()
        val kindType = KindType.values().any { it.name == "value" }
        return if (kindType) delegate.fromJsonValue(value) else null
    }
}
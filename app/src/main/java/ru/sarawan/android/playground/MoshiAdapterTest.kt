package ru.sarawan.android.playground

import com.squareup.moshi.JsonAdapter

import com.squareup.moshi.Moshi
import java.lang.reflect.Type


var json = "{'brand': 'Befruitbe', 'manufacturer': 'Basilur Tea Exports', 'manufacturing_country': 'Шри-Ланка', 'Целевая аудитория': 'Взрослые', 'Тип продукта': 'Чай', 'Тип чая': 'Черный', 'Форма': 'Листовой', 'Размер листа': 'Среднелистовой', 'Особенность состава': 'Без ароматизаторов', 'Комплектация': 'Кружка', 'Вес': '30 г', 'Особенности упаковки': 'Подарочная', 'Условия хранения': 'В сухом прохладном месте и относительной влажности воздуха не более 70%.'}"
fun main() {
    val moshi: Moshi = Moshi.Builder()
        .add(LENIENT_FACTORY)
        .build()
    val jsonAdapter: JsonAdapter<Information> = moshi.adapter(Information ::class.java)
    val blackjackHand = jsonAdapter.fromJson(json)
    println(blackjackHand?.brand + " " + blackjackHand?.manufacturer)
}


data class Information(val brand : String, val manufacturer : String)

val LENIENT_FACTORY: JsonAdapter.Factory = object : JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: Set<Annotation>,
        moshi: Moshi
    ): JsonAdapter<Information>? {
        return moshi.nextAdapter<Information>(this, type, annotations).lenient()
    }
}
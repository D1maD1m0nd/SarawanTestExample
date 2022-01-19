package ru.sarawan.android.playground

import com.squareup.moshi.JsonAdapter

import com.squareup.moshi.Moshi






val json = "{\n" +
        "   \"brand\":\"Befruitbe\",\n" +
        "   \"manufacturer\":\"Basilur Tea Exports\",\n" +
        "   \"manufacturing_country\":\"Шри-Ланка\",\n" +
        "   \"Целевая аудитория\":\"Взрослые\",\n" +
        "   \"Тип продукта\":\"Чай\",\n" +
        "   \"Тип чая\":\"Черный\",\n" +
        "   \"Форма\":\"Листовой\",\n" +
        "   \"Размер листа\":\"Среднелистовой\",\n" +
        "   \"Особенность состава\":\"Без ароматизаторов\",\n" +
        "   \"Комплектация\":\"Кружка\",\n" +
        "   \"Вес\":\"30 г\",\n" +
        "   \"Особенности упаковки\":\"Подарочная\",\n" +
        "   \"Условия хранения\":\"В сухом прохладном месте и относительной влажности воздуха не более 70%.\"\n" +
        "}"
fun main() {
    val moshi: Moshi = Moshi.Builder()
        .build()

    val jsonAdapter: JsonAdapter<Information> = moshi.adapter(Information ::class.java)
    val blackjackHand = jsonAdapter.fromJson(json)
    println(blackjackHand?.brand + " " + blackjackHand?.manufacturer)
}


data class Information(val brand : String, val manufacturer : String)


package com.example.sarawan.utils.exstentions

fun Double.toFormatString(postfix : String)  = String.format("%.2f $postfix", this)

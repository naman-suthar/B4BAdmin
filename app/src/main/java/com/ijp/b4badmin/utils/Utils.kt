package com.ijp.b4badmin.utils

import java.text.SimpleDateFormat
import java.util.*

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
    return format.format(date)
}

sealed class TaskEarningType(val type: String){
    object Fixed : TaskEarningType("Fixed")
    object Percentage : TaskEarningType("Percentage")
}
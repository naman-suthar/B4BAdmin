package com.ijp.b4badmin.model

data class Question (
    var qid: String? = null,
    var question_statement: String? = null,
    var question_type: String? = null,
    var options: List<String>? = null
        )


sealed class QuestionType(val type: String){
    object Text: QuestionType("text")
    object MultiLineText: QuestionType("multilineText")
    object Boolean: QuestionType("boolean")
    object Number: QuestionType("number")
    object Dropdown: QuestionType("dropdown")
    object Ratings: QuestionType("ratings")
    object Photo: QuestionType("photo")
}

fun stringToQuestionType(str: String){
    when(str){
        "text" -> QuestionType.Text
        "multilineText" -> QuestionType.MultiLineText
        "boolean" -> QuestionType.Boolean
        "number" -> QuestionType.Number
        "dropdown" -> QuestionType.Dropdown
        "ratings" -> QuestionType.Ratings
        "photo" -> QuestionType.Photo
    }
}
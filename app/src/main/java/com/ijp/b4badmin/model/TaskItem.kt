package com.ijp.b4badmin.model

data class TaskItem(
    var taskId: String? = null,
    var jobId: String? = null,
    var task_title: String? = null,
    var task_tagline:String? = null,
    var task_earning_price: Int? = null,
    var screeningQuestions: List<Question>? = null,
    var assessmentQuestions: List<Question>? = null,
    var task_steps_to_follow: String? = null,
    var task_guidelines: String? = null,
    var task_note: String? = null,
    var training_note: String? = null,
    var training_video_ID: String? = null
)

fun getAirtelTask(): TaskItem {
    return TaskItem(
        "Tsk001",
        "IDJ1",
        "Merchant Acquisition",
        "Setup QR Codes",
        50
    )
}
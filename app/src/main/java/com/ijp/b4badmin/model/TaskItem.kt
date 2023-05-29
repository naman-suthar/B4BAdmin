package com.ijp.b4badmin.model

data class TaskItem(
    var taskId: String? = null,
    var jobId: String? = null,
    var jobLogo: String? = null,
    var task_title: String? = null,
    var task_tagline:String? = null,
    var price_tagline: String? = null,
    var task_earning_price: Float? = null,
    var screeningQuestions: List<Question>? = null,
    var assessmentQuestions: List<Question>? = null,
    var task_steps_to_follow: String? = null,
    var task_guidelines: String? = null,
    var task_note: String? = null,
    var training_note: String? = null,
    var training_video_ID: String? = null,
    var task_qr_url: String? = null,
    var price_type: String? = null,
    var no_of_images_proof: Int? = null,
    var principal_info_note: String? = null
//    var principle_amount: Float? = null, // Variable Principle for %type
//    var percentage_price: Float? = null // % on %type
):java.io.Serializable

fun getAirtelTask(): TaskItem {
    return TaskItem(
        "Tsk001",
        "IDJ1",
        "",
        "Merchant Acquisition",
        "Setup QR Codes",
        "xyz",
        50f,
    )
}
package com.ijp.b4badmin.model

import android.os.Parcelable


data class Job(
    var job_id: String? = null,
    var job_title: String? = null,
    var job_type: String? = null,   //WorkFromHome, OnField etc
    var job_tagline: String? = null,
    var job_qualification: List<String>? = null,
    var job_description: String? = null,
    var job_roles_and_responsibilities: List<String>? = null,
    var salary_and_other_benefits: List<String>? = null,
    var associatedTasks: List<TaskItem>? = null,
    var screeningQuestions: List<Question>? = null,
    var applied: Boolean? = null,
    var requirements: Map<String,String>? = null,
    var job_icon: String? = null,
    var status: String? = null
): java.io.Serializable

fun getAirtelJob(): Job {
    return Job(
        "IDJ1",
        "Airtel",
        "On Field",
        "Earn Upto 1,00,000 per month ",
        listOf(
            "18+",
            "qualification 2",
            "qualification 3"
        ),
        "This is the description of Job",
        listOf(
            "Role no 1",
            "Role no 2"
        ),
        listOf(
            "Salary",
            "Benefit No 1"
        )
, associatedTasks = listOf(getAirtelTask())
    )
}

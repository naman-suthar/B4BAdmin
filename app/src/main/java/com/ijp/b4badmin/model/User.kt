package com.ijp.b4badmin.model

data class User(
        val id: String? = null,
        var name: String? = null,
        var phoneNo: String? = null,
        var dob: String? = null,
        var email: String? = null,
        var hasRegistered: Boolean? = false,
        var qualification: Qualification? =null,
        var additionalInfo: AdditionalInfo? = null,
        var profile_picture: String? = null,
        var approved_jobs: HashMap<String,String>? = null,
        var network: List<HashMap<String,String>>? = null
): java.io.Serializable


data class Qualification(
        var institute_name: String? = null,
        var degree: String? = null,
        var specialization: String? = null,
        var starts_from: String? = null,
        var ends_on: String? = null
)

data class AdditionalInfo(
        var alternateNo: String? = null,
        var address: String? = null,
        var language: String? = null,
        var pinCode: String? = null
)

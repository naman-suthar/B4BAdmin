package com.ijp.b4badmin.model

data class KYCDetails(
    var id: String? =null,
    var user_name: String? = null,
    var user_dob: String? = null,
    var pan_card: String? = null,
    var adhaar_card: String? = null,
    var driving_license_no: String? = null,
    var user_gender: String? = null,
    var status: String? = null
)

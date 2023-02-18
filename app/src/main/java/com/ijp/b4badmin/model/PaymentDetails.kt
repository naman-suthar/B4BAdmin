package com.ijp.b4badmin.model

data class PaymentDetails(
    var id: String? = null,
    var selected_mode: String? = null,
    var upiId: String? = null,
    var bank_account_name: String? = null,
    var bank_account_number: String? = null,
    var bank_account_type: String? = null,
    var bank_ifsc: String? = null,
    var activation_time: Long?= null
)

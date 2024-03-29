package com.ijp.b4badmin.model

data class WithdrawalRequest(
    var id: String? = null,
    var amount: Int? = null,
    var user_id: String? = null,
    var time_of_request: Long? = null,
    var time_of_approval: Long? = null,
    var status: String? = null,
    var userName: String? = null,
    var transaction_id: String? = null,
    var employee_id: String? = null,
    var rejection_message: String? = null
):java.io.Serializable

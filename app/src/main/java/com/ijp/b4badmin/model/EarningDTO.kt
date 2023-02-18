package com.ijp.b4badmin.model

data class EarningDTO(
    var userid: String? = null,
    var balance: Int? = null,
    var total_earning: Int? = null,
    var total_pending: Int? = null,
    var total_withdrawal: Int? = null,
    var pending_withdrawal:Int? = null,
    var withdrawalHistory: MutableList<WithdrawalRequest>? = null,
    )

data class Transaction(
    var tid:String? = null,
    var transaction_type: String? = null,
    var amount: String? =null,
    var userid: String? = null,
    var time_of_request: Long? = null,
    var time_of_approval: Long? = null,
    var associated_job_id: String? = null,
    var associated_task_id: String? = null  //for earning task
)
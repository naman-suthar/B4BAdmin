package com.ijp.b4badmin.model

data class EarningDTO(
    var userid: String? = null,
    var balance: Long? = null,
    var total_earning: Long? = null,
    var total_pending: Long? = null,
    var total_withdrawal: Long? = null,
    var pending_withdrawal:Long? = null,
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
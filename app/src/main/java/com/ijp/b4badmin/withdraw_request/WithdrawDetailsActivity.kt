package com.ijp.b4badmin.withdraw_request

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import com.ijp.b4badmin.model.EarningDTO
import com.ijp.b4badmin.model.WithdrawalRequest
import com.vrcareer.b4badmin.databinding.ActivityWithdrawDetailsBinding
import java.text.SimpleDateFormat
import java.util.*
/**
 * This acticty is used to display details of the Withdrawal Request and Approve/Reject
 * */
class WithdrawDetailsActivity : AppCompatActivity() {
    private var binding: ActivityWithdrawDetailsBinding? = null
    private val db = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if (intent!=null){
            val withdrawalRequest = intent.getSerializableExtra("request") as WithdrawalRequest
            binding?.topAppBar?.title = withdrawalRequest.userName
            binding?.txtWithdrawAmount?.text = withdrawalRequest.amount.toString()
            binding?.txtWithdrawRequestTime?.text = convertLongToTime( withdrawalRequest.time_of_request!!)

            binding?.btnRequestApproved?.setOnClickListener {
                val progressDialog = ProgressDialog(this)
                progressDialog.setCancelable(false)
                progressDialog.show()

                var finish = false
                /**
                 * Fetch Earning and update the earning after Approval*/
                db.reference.child("earnings").child(withdrawalRequest.user_id!!).runTransaction(
                    object : Transaction.Handler{
                        override fun doTransaction(currentData: MutableData): Transaction.Result {
                            currentData.value?.let {
                                val currentEarning = currentData.getValue(EarningDTO::class.java)
                                val withdrawHistory = currentEarning?.withdrawalHistory ?: mutableListOf()
                                withdrawHistory.add(withdrawalRequest)
                                val newEarning = currentEarning?.copy(
                                    balance = currentEarning.balance?.minus(withdrawalRequest.amount!!),
                                    pending_withdrawal= currentEarning.pending_withdrawal?.minus(withdrawalRequest.amount!!),
                                    total_withdrawal = currentEarning.total_withdrawal?.plus(withdrawalRequest.amount!!),
                                    withdrawalHistory = withdrawHistory
                                )
                                currentData?.value = newEarning
                                finish = true
                            }

                            return Transaction.success(currentData)
                        }

                        override fun onComplete(
                            error: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                        ) {
                            if (finish){

                                db.reference.child("withdraw_request").child(withdrawalRequest.user_id!!).removeValue().addOnSuccessListener {
                                    progressDialog.dismiss()
                                    finish()
                                }
                            }
                        }
                    }
                )
            }
            binding?.btnRequestDecline?.setOnClickListener {
                finish()
            }
        }

    }
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
    return format.format(date)
}
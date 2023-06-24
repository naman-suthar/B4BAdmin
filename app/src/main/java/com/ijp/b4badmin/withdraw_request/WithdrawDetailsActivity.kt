package com.ijp.b4badmin.withdraw_request

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.ijp.b4badmin.model.EarningDTO
import com.ijp.b4badmin.model.WithdrawalRequest
import com.vrcareer.b4badmin.R
import com.vrcareer.b4badmin.databinding.ActivityWithdrawDetailsBinding
import java.text.SimpleDateFormat
import java.util.*
/**
 * This acticty is used to display details of the Withdrawal Request and Approve/Reject
 * */
class WithdrawDetailsActivity : AppCompatActivity() {
    private var binding: ActivityWithdrawDetailsBinding? = null
    private val db = FirebaseDatabase.getInstance()
    private var alertDialog: AlertDialog? = null
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
                val dialog = MaterialAlertDialogBuilder(this@WithdrawDetailsActivity)
                dialog.setTitle("Add Payment Information")
                val view =
                    LayoutInflater.from(this@WithdrawDetailsActivity)
                        .inflate(R.layout.withdrawal_approve_dialog_for_adding_info, null, false)
                dialog.setView(view)

                val transactionID = view.findViewById<TextView>(R.id.et_dialog_transaction_id)
                val employeeID = view.findViewById<TextView>(R.id.et_dialog_employee_id)
                dialog.setCancelable(false)
                dialog.setPositiveButton("Confirm", null)
                    .setNegativeButton("Cancel") { d, _ ->
                        d.dismiss()
                    }
                alertDialog = dialog.create()
                alertDialog?.show()
                val positiveButton = alertDialog?.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                positiveButton?.setOnClickListener{
                    val transactionId = transactionID.text.toString()
                    val employeeId = employeeID.text.toString()
                    if (transactionId.isEmpty()){
                        transactionID.error = "Enter Transaction ID"
                        transactionID.requestFocus()
                        return@setOnClickListener
                    }
                    if (employeeId.isEmpty()){
                        employeeID.error = "Enter Employee ID"
                        employeeID.requestFocus()
                        return@setOnClickListener
                    }
                    withdrawalRequest.transaction_id = employeeId
                    withdrawalRequest.employee_id = employeeId
                    withdrawalRequest.status = "Approved"


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
                                    val currentTime = System.currentTimeMillis()

                                    db.reference.child("withdrawal_history").child(withdrawalRequest.user_id!!).child(currentTime.toString()).setValue(withdrawalRequest).addOnSuccessListener {
                                        db.reference.child("withdraw_request").child(withdrawalRequest.user_id!!).removeValue().addOnSuccessListener {
                                            alertDialog?.dismiss()
                                            progressDialog.dismiss()
                                            finish()
                                        }

                                    }
                                }
                            }
                        }
                    )
                }


            }
            binding?.btnRequestDecline?.setOnClickListener {
                val dialog = MaterialAlertDialogBuilder(this@WithdrawDetailsActivity)
                dialog.setTitle("Add Payment Information")
                val view =
                    LayoutInflater.from(this@WithdrawDetailsActivity)
                        .inflate(R.layout.dialog_reject_task, null, false)
                dialog.setView(view)
                val rejectionMessageEt = view.findViewById<TextView>(R.id.et_reject_message)
                dialog.setCancelable(false)
                dialog.setPositiveButton("Confirm", null)
                    .setNegativeButton("Cancel") { d, _ ->
                        d.dismiss()
                    }
                alertDialog = dialog.create()
                alertDialog?.show()
                val positiveButton = alertDialog?.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                positiveButton?.setOnClickListener{
                    if (rejectionMessageEt.text.toString().isEmpty()){
                        rejectionMessageEt.error = "Enter Rejection Message"
                        rejectionMessageEt.requestFocus()
                        return@setOnClickListener
                    }
                    withdrawalRequest.status = "Rejected"
                    withdrawalRequest.transaction_id = "N/A"
                    withdrawalRequest.employee_id = "N/A"
                    withdrawalRequest.rejection_message = rejectionMessageEt.text.toString()
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    val currentTime = System.currentTimeMillis()
                    db.reference.child("withdrawal_history").child(withdrawalRequest.user_id!!).child(currentTime.toString()).setValue(withdrawalRequest).addOnSuccessListener {
                        db.reference.child("withdraw_request").child(withdrawalRequest.user_id!!).removeValue().addOnSuccessListener {
                            alertDialog?.dismiss()
                            progressDialog.dismiss()
                            finish()
                        }
                    }
                }

            }
        }

    }
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
    return format.format(date)
}
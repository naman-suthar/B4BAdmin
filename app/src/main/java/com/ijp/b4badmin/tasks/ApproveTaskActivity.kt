package com.ijp.b4badmin.tasks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.vrcareer.b4badmin.R
import com.vrcareer.b4badmin.databinding.ActivityApproveTaskBinding
import com.ijp.b4badmin.jobs.job_application.RvApplicationAnswersAdapter
import com.ijp.b4badmin.model.EarningDTO
import com.ijp.b4badmin.model.SubmittedTask
import com.ijp.b4badmin.model.User

/**
 * This activity is used to display details of pending task and approval/rejection */
class ApproveTaskActivity : AppCompatActivity() {
    private var binding: ActivityApproveTaskBinding? = null
    private val db = FirebaseDatabase.getInstance()
    private var alertDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApproveTaskBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        Log.d("ReceivedTask ","${intent.getSerializableExtra("task")}")
        val task = intent.getSerializableExtra("task") as SubmittedTask?

        task?.uid?.let {
            db.reference.child("users").child(it).get().addOnSuccessListener { ds ->
                if (ds.exists()) {
                    val user = ds.getValue(User::class.java)
                    binding?.applicationDetailsUserName?.text = user?.name
                    binding?.applicationDetailsUserEmail?.text = user?.email
                    binding?.applicationDetailsUserMobile?.text = user?.phoneNo
                    binding?.applicationDetailsUserDob?.text = user?.dob
                }
            }
        }
        binding?.jobIdText?.text = "Task ID: ${task?.taskId}"
        for (i in 0 until (task?.imageList?.size ?: 0)) {
            val imageView = ImageView(this)
            imageView.load(task?.imageList?.get(i)?.toUri())
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0,6,0,6)
            binding?.llImageHolder?.addView(imageView,layoutParams)
        }

        if (task?.imageList?.isEmpty() == true) {
            binding?.txtImageProofHeading?.text = "No Image Proof"
        }else{
            binding?.txtImageProofHeading?.text = "Image Proofs"
        }

        val adapter = RvApplicationAnswersAdapter(this,task?.answerList!!)
        binding?.rvSubmittedTaskAnswersRv?.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }
        binding?.btnRejectTask?.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_reject_task,null,false)
            dialog.setTitle("Rejection Message")
            dialog.setView(view)
            val etMessage: EditText = view.findViewById(R.id.et_reject_message)
            dialog.setCancelable(false)
            dialog.setPositiveButton("Confirm",null)
                .setNegativeButton("Cancel") { d, _ ->
                    d.dismiss()
                }
            alertDialog = dialog.create()
            alertDialog?.show()
            val positiveButton = alertDialog?.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            positiveButton?.setOnClickListener {
                val rejectMessage = etMessage.text.toString().trim()
                if (rejectMessage.isNotEmpty()) {
                    val userId = task?.uid
                    val taskId = task?.taskId
                    if (userId != null) {
                        var finish = false
                        db.reference.child("submitted_task").child(userId).child(task?.uniqueId!!)
                            .runTransaction(
                                object : Transaction.Handler{
                                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                                        currentData.value?.let {
                                            val currentDataInDb = currentData.getValue(SubmittedTask::class.java)
                                            if (currentDataInDb?.status != "rejected"){
                                                val newData = currentDataInDb?.copy(
                                                    status = "rejected",
                                                    message = rejectMessage
                                                )
                                                currentData.value = newData
                                                finish = true
                                            }


                                        }
                                        return Transaction.success(currentData)
                                    }

                                    override fun onComplete(
                                        error: DatabaseError?,
                                        committed: Boolean,
                                        currentData: DataSnapshot?
                                    ) {
                                        if (error != null) {
                                            Log.d("Firebase", "Transaction failed")
                                        } else {
                                            if (finish){
                                                alertDialog?.dismiss()
                                                finish()
                                            }
                                            Toast.makeText(this@ApproveTaskActivity,"Rejected",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                    }

                } else {
                    //Prompt error
                    etMessage.requestFocus()
                    etMessage.error = "Please enter valid message"
                }
            }

        }
        binding?.btnApproveTask?.setOnClickListener {
            // ToDO
            //Status of task -> Approve
            val userId = task.uid
            val taskId = task.taskId

            if (userId != null) {
                Log.d("FirebaseDB:","${task.uniqueId}")

                db.reference.child("submitted_task").child(userId).child(task.uniqueId!!)
                    .child("status")
                    .get()
                    .addOnSuccessListener {
                        if (it.exists()){
                            var finish = false
                            val status = it.value
                            if (status!="approved"){
                                db.reference.child("submitted_task").child(userId).child(task.uniqueId!!)
                                    .child("status")
                                    .setValue("approved").addOnSuccessListener {

                                        val myRef = db.getReference("earnings/$userId")

                                        myRef.runTransaction(object : Transaction.Handler {
                                            override fun doTransaction(mutableData: MutableData): Transaction.Result {

                                                mutableData?.value?.let {
                                                    val currentEarning = mutableData.getValue(EarningDTO::class.java)
                                                    val newEarning = currentEarning?.copy(
                                                        balance = currentEarning.balance?.plus(task.associated_amount!!),
                                                        total_pending = currentEarning.total_pending?.minus(task.associated_amount!!),
                                                        total_earning = currentEarning.total_earning?.plus(task.associated_amount!!)
                                                    )
                                                    Log.d("mutableData:","$mutableData \n CE $currentEarning \n NE: $newEarning")
                                                    mutableData?.value = newEarning
                                                    finish = true
                                                }
                                                return Transaction.success(mutableData)

                                            }

                                            override fun onComplete(
                                                databaseError: DatabaseError?,
                                                committed: Boolean,
                                                currentData: DataSnapshot?
                                            ) {
                                                if (databaseError != null) {
                                                    Log.d("Firebase", "Transaction failed")
                                                } else if (finish) {
                                                    Toast.makeText(this@ApproveTaskActivity,"Approved Earning",Toast.LENGTH_SHORT).show()
                                                    this@ApproveTaskActivity.finish()
                                                }
                                            }
                                        })
                                    }
                            }
                            else{
                                finish()
                            }
                        }
                    }

            }
            //Earning Added to user Profile

        }
    }
    fun updateData(newValue: Int) {


    }
}
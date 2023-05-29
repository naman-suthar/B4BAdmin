package com.ijp.b4badmin.trainings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vrcareer.b4badmin.databinding.ActivityApproveAssessmentBinding
import com.ijp.b4badmin.jobs.job_application.RvApplicationAnswersAdapter
import com.ijp.b4badmin.model.Assessment
import com.ijp.b4badmin.model.User
/**
 * This Activity is used to Show the submitted answer in Assessment and Accept/Reject
 * */
class ApproveAssessmentActivity : AppCompatActivity() {
    private var binding: ActivityApproveAssessmentBinding? = null
    private val db = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApproveAssessmentBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if (intent != null) {

            val assessment = intent.getSerializableExtra("assessment") as Assessment
            /**
             * Fetching the Assessment Status and if already responded then Buttons are disabled*/
            val assessmentStatusRef = db.reference.child("trainings").child(assessment.user_id!!)
                .child(assessment.task_id!!).child("status")
            assessmentStatusRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val status = snapshot.value
                        if (status == "pending"){
                            binding?.btnAssessmentApproved?.isEnabled = true
                            binding?.btnAssessmentRejected?.isEnabled = true
                        }
                        else
                        {
                            binding?.btnAssessmentApproved?.isEnabled = false
                            binding?.btnAssessmentRejected?.isEnabled = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
            assessment.user_id?.let { userid ->
                db.reference.child("users").child(userid).get().addOnSuccessListener { ds ->
                    if (ds.exists()) {
                        val user = ds.getValue(User::class.java)
                        binding?.applicationDetailsUserName?.text = user?.name
                        binding?.applicationDetailsUserEmail?.text = user?.email
                        binding?.applicationDetailsUserMobile?.text = user?.phoneNo
                        binding?.applicationDetailsUserDob?.text = user?.dob
                    }
                }
            }

            assessment?.ansList?.let { list ->
                binding?.rvApplicationAnswers?.let { rv ->
                    rv.layoutManager = LinearLayoutManager(this)
                    rv.adapter = RvApplicationAnswersAdapter(this, list)
                }

                binding?.taskIdAssessment?.text = assessment?.task_id
                binding?.jobIdAssessment?.text = assessment?.job_id
            }


            binding?.btnAssessmentApproved?.setOnClickListener {
                    assessmentStatusRef.setValue("approved").addOnSuccessListener {
                        Toast.makeText(this, "Approved", Toast.LENGTH_SHORT).show()
                    }
            }
            binding?.btnAssessmentRejected?.setOnClickListener {
                assessmentStatusRef.setValue("rejected")
                    .addOnSuccessListener {
                        Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show()
                    }
            }
        }


    }
}
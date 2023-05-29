package com.ijp.b4badmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

import com.ijp.b4badmin.jobs.JobHomeActivity
import com.ijp.b4badmin.my_users.MyUsersActivity
import com.ijp.b4badmin.tasks.TasksHomeActivity
import com.ijp.b4badmin.trainings.TrainingSubmissionListActivity
import com.ijp.b4badmin.withdraw_request.WithdrawRequestActivity
import com.vrcareer.b4badmin.databinding.ActivityMainBinding

/**
 * This is Home Screen It contains all Cards*/
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var db = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
      binding?.mcJobs?.setOnClickListener {
          val intent = Intent(this,JobHomeActivity::class.java)
          startActivity(intent)
      }
        binding?.mcUsers?.setOnClickListener {
            val intent = Intent(this,MyUsersActivity::class.java)
            startActivity(intent)
        }
        binding?.mcTasks?.setOnClickListener {
            val intent = Intent(this,TasksHomeActivity::class.java)
            startActivity(intent)
        }
        binding?.btnTrainings?.setOnClickListener {
            val intent = Intent(this,TrainingSubmissionListActivity::class.java)
            startActivity(intent)
        }
        binding?.mcWithdrawRequest?.setOnClickListener {
            val intent = Intent(this,WithdrawRequestActivity::class.java)
            startActivity(intent)
        }
    }
}
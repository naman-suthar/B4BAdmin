package com.ijp.b4badmin.jobs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ijp.b4badmin.jobs.configure.ConfigureAppActivity
import com.ijp.b4badmin.jobs.job_application.AddNewJobActivity
import com.vrcareer.b4badmin.databinding.ActivityJobHomeBinding

/**
 * This is Jobs Home Activity The options related to jobs appears here
 * */
class JobHomeActivity : AppCompatActivity() {
    private var binding: ActivityJobHomeBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.mcJobApplication?.setOnClickListener {
            val intent = Intent(this,JobApplicationActivity::class.java)
            startActivity(intent)
        }
        binding?.mcAddTask?.setOnClickListener {
            val intent = Intent(this,JobAddTaskActivity::class.java)
            startActivity(intent)
        }
        binding?.mcAddJob?.setOnClickListener {
            val intent = Intent(this,AddNewJobActivity::class.java)
            startActivity(intent)
        }
        binding?.mcJobConfigure?.setOnClickListener {
            val intent = Intent(this,ConfigureAppActivity::class.java)
            startActivity(intent)
        }
    }
}
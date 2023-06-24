package com.ijp.b4badmin.jobs.configure.task

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import com.ijp.b4badmin.model.Job
import com.vrcareer.b4badmin.R
import com.vrcareer.b4badmin.databinding.ActivitySelectJobsFromAllAvailableBinding

class SelectJobsFromAllAvailableActivity : AppCompatActivity() {
    private var binding: ActivitySelectJobsFromAllAvailableBinding? = null
    private val db = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectJobsFromAllAvailableBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        db.reference.child("Jobs").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val jobList = mutableListOf<Job>()
                for (job in snapshot.children) {
                    val jobItem = job.getValue(Job::class.java)
                    if (jobItem != null) {
                        jobList.add(jobItem)
                    }
                }
                binding?.frameItemLoadingSelectJob?.visibility = View.GONE
                binding?.let { b ->
                    b.rvJobsItem?.let { rv ->
                        rv.layoutManager =
                            LinearLayoutManager(this@SelectJobsFromAllAvailableActivity)
                        rv.adapter = RvAllJobAdapter(
                            this@SelectJobsFromAllAvailableActivity,
                            jobList
                        ) { job ->
                                val intent = Intent(this@SelectJobsFromAllAvailableActivity,ConfigureTaskListActivity::class.java)
                                intent.putExtra("jobId",job.job_id)
                                startActivity(intent)
                        }
                    }
                }
            } else {
                binding?.progress?.visibility = View.GONE
                Toast.makeText(
                    this@SelectJobsFromAllAvailableActivity,
                    "No Job Applications available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

class RvAllJobAdapter(
    val context: Context,
    val jobList: List<Job>,
    val onJobItemClicked: (Job) -> Unit
) : RecyclerView.Adapter<RvAllJobAdapter.RvJobListItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvJobListItem {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.job_application_job_option_item, parent, false)
        return RvJobListItem(view)
    }

    override fun onBindViewHolder(holder: RvJobListItem, position: Int) {
        val currJob = jobList[position]
        holder.frameLayout.visibility = View.GONE
        holder.jobTitle.text = currJob?.job_title
        holder.jobLogo.load(currJob?.job_icon)
        holder.mcJopOption.setOnClickListener {
            onJobItemClicked(currJob)
        }

    }

override fun getItemCount(): Int = jobList.size

class RvJobListItem(view: View) : RecyclerView.ViewHolder(view) {
    val jobTitle: TextView = view.findViewById(R.id.tv_job_option_title)
    val frameLayout: FrameLayout = view.findViewById(R.id.frame_item_loading)
    val mcJopOption: MaterialCardView = view.findViewById(R.id.mc_job_item_option)
    val jobLogo: ImageView = view.findViewById(R.id.img_logo_job_option)
}



}
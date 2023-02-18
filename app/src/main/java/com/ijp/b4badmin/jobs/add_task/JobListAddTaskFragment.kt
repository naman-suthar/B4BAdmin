package com.ijp.b4badmin.jobs.add_task

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.ijp.b4badmin.R
import com.ijp.b4badmin.databinding.FragmentJobListAddTaskBinding
import com.ijp.b4badmin.jobs.job_application.JobsListOptionFragmentDirections
import com.ijp.b4badmin.jobs.job_application.RvJobOptionAdapter


class JobListAddTaskFragment : Fragment() {
    private var binding: FragmentJobListAddTaskBinding? = null
    private var db = FirebaseDatabase.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobListAddTaskBinding.inflate(inflater,container,false)
        db.reference.child("Jobs").get().addOnSuccessListener {
            if (it.exists()){
                val jobList = mutableListOf<String>()
                for(jobId in it.children){
                    val job = jobId.key.toString()
                    Log.d("JobId", job)
                    jobList.add(job)
                }
                binding?.frameItemLoadingSelectJobAddTask?.visibility = View.GONE
                binding?.let { b->
                    b.rvJobsItemAddTask?.let { rv->
                        rv.layoutManager = LinearLayoutManager(context)
                        rv.adapter = context?.let { it1 -> RvJobOptionAdapter(it1,jobList){job_id->
                            val action = JobsListOptionFragmentDirections.actionJobsListOptionFragmentToJobListFragment(job_id)
                            findNavController().navigate(action)
                        } }
                    }
                }
            }
        }
        return binding?.root
    }


}
package com.ijp.b4badmin.jobs.job_application

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import com.ijp.b4badmin.R
import com.ijp.b4badmin.databinding.FragmentJobListBinding
import com.ijp.b4badmin.model.JobApplication
import com.ijp.b4badmin.model.User
import com.ijp.b4badmin.utils.convertLongToTime

/**
 * This fragment shows Job application for selected job
 * */
class JobListFragment : Fragment() {
    private var binding: FragmentJobListBinding? = null
    private val db = FirebaseDatabase.getInstance()
    private val args: JobListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobListBinding.inflate(inflater, container, false)
        val jobId = args.jobId
        db.reference.child("job_application").child(jobId).child("pending").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val applicationList = mutableListOf<JobApplication>()
                    for (application in it.children) {
                        val jobApplication = application.getValue(JobApplication::class.java)
                        if (jobApplication != null) {
                            applicationList.add(jobApplication)
                        }
                    }
                    binding?.rvApplicationList?.let { rv->
                        rv.layoutManager = LinearLayoutManager(context)
                        rv.adapter =
                            context?.let { it1 -> RvJobApplicationListAdapter(it1,applicationList){application->
                                Log.d("JobClicked","Clicked $application")
                                val action = JobListFragmentDirections.actionJobListFragmentToJobApplicationDetailsFragment(
                                    application
                                )
                                findNavController().navigate(action)
                            } }
                    }

                }
            }
        return binding?.root
    }

}

class RvJobApplicationListAdapter(
    context: Context, private val jobApplicationList: List<JobApplication>,
    var onApplicationClicked: (JobApplication) -> Unit
) :RecyclerView.Adapter<RvJobApplicationListAdapter.RvJobApplicationViewHolder>(){
    private val db = FirebaseDatabase.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvJobApplicationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_job_application_user_item,parent,false)
        return  RvJobApplicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvJobApplicationViewHolder, position: Int) {
        val currApplication = jobApplicationList[position]
        val time = currApplication.time_of_request?.let { convertLongToTime(it) }
        holder.tvTimeRequset.text = if (!time.isNullOrEmpty()) time else "dd/mm/yyyy"
        currApplication.user_id?.let { db.reference.child("users").child(it).get().addOnSuccessListener { ds->
            if (ds.exists()){
                holder.frameLayout.visibility = View.GONE
                val user = ds.getValue(User::class.java)
                holder.userName.text = user?.name
                holder.mcJoApplication.setOnClickListener {
                    onApplicationClicked(currApplication)
                }
            }
        } }
    }

    override fun getItemCount(): Int = jobApplicationList.size

    class RvJobApplicationViewHolder(view:View): RecyclerView.ViewHolder(view){
        val userName: TextView = view.findViewById(R.id.tv_application_user_name)
        val tvTimeRequset: TextView = view.findViewById(R.id.tv_time_of_request)
        val frameLayout: FrameLayout = view.findViewById(R.id.frame_item_loading_job_application_user_list)
        val mcJoApplication: MaterialCardView = view.findViewById(R.id.mc_application_item)
    }
}
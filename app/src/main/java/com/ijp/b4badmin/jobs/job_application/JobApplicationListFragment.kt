package com.ijp.b4badmin.jobs.job_application

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import com.ijp.b4badmin.model.JobApplication
import com.ijp.b4badmin.model.User
import com.ijp.b4badmin.utils.ApplicationResponse
import com.ijp.b4badmin.utils.convertLongToTime
import com.vrcareer.b4badmin.R
import com.vrcareer.b4badmin.databinding.FragmentJobListBinding

/**
 * This fragment shows Job application for selected job
 * */
class JobListFragment : Fragment() {
    private var binding: FragmentJobListBinding? = null
    private val db = FirebaseDatabase.getInstance()
    private val args: JobListFragmentArgs by navArgs()
    private var applicationList = mutableListOf<JobApplication>()
    private var filter = ApplicationResponse.Pending.name
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobListBinding.inflate(inflater, container, false)
        val jobId = args.jobId
        db.reference.child("job_application").child(jobId).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    applicationList = mutableListOf()
                    for (application in it.children) {
                        val jobApplication = application.getValue(JobApplication::class.java)
                        if (jobApplication != null) {
                            applicationList.add(jobApplication)
                        }
                    }
                    Log.d("ApplicationList", applicationList.toString())
                    binding?.rvApplicationList?.let { rv ->
                        rv.layoutManager = LinearLayoutManager(context)
                        rv.adapter =
                            context?.let { it1 ->

                                val applist =  applicationList.filter { application ->
                                    application.status == filter
                                }
                                if (applist.isEmpty()) binding?.frameNoText?. visibility = View.VISIBLE
                                else binding?.frameNoText?.visibility = View.GONE

                                RvJobApplicationListAdapter(
                                    it1,
                                    applist
                                   ) { application ->
                                    Log.d("JobClicked", "Clicked $application")
                                    val action =
                                        JobListFragmentDirections.actionJobListFragmentToJobApplicationDetailsFragment(
                                            application
                                        )
                                    findNavController().navigate(action)
                                }
                            }
                    }

                }
            }

        binding?.let { b ->
            b.chipPending.setOnClickListener {
                b.chipApproved.isChecked = false
                b.chipPending.isChecked = true
                b.chipRejected.isChecked = false
                filter = ApplicationResponse.Pending.name
                val appList = applicationList.filter { application ->
                    application.status == filter
                }
                b.rvApplicationList.adapter?.let { adapter ->
                    if (adapter is RvJobApplicationListAdapter) {
                        adapter.updateList(appList)
                    }
                }
                if (appList.isEmpty()) b.frameNoText. visibility = View.VISIBLE
                else b.frameNoText.visibility = View.GONE
            }

            b.chipApproved.setOnClickListener {
                b.chipApproved.isChecked = true
                b.chipPending.isChecked = false
                b.chipRejected.isChecked = false
                filter = ApplicationResponse.Approved.name
                val appList = applicationList.filter { application ->
                    application.status == filter
                }
                b.rvApplicationList.adapter?.let { adapter ->
                    if (adapter is RvJobApplicationListAdapter) {
                        adapter.updateList(
                            appList
                        )
                    }
                }

                if (appList.isEmpty()) b.frameNoText. visibility = View.VISIBLE
                else b.frameNoText.visibility = View.GONE
            }
            b.chipRejected.setOnClickListener {
                b.chipApproved.isChecked = false
                b.chipPending.isChecked = false
                b.chipRejected.isChecked = true
                filter = ApplicationResponse.Rejected.name
                val appList = applicationList.filter { application ->
                    application.status == filter
                }
                b.rvApplicationList.adapter?.let { adapter ->
                    if (adapter is RvJobApplicationListAdapter) {
                        adapter.updateList(
                            appList
                        )
                    }
                }
                if (appList.isEmpty()) b.frameNoText. visibility = View.VISIBLE
                else b.frameNoText.visibility = View.GONE
            }
        }
        return binding?.root
    }

}

class RvJobApplicationListAdapter(
    context: Context, private var jobApplicationList: List<JobApplication>,
    var onApplicationClicked: (JobApplication) -> Unit
) : RecyclerView.Adapter<RvJobApplicationListAdapter.RvJobApplicationViewHolder>() {
    private val db = FirebaseDatabase.getInstance()

    fun updateList(list: List<JobApplication>) {
        jobApplicationList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvJobApplicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_job_application_user_item, parent, false)
        return RvJobApplicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvJobApplicationViewHolder, position: Int) {
        val currApplication = jobApplicationList[position]
        val time = currApplication.time_of_request?.let { convertLongToTime(it) }
        holder.tvTimeRequset.text = if (!time.isNullOrEmpty()) time else "dd/mm/yyyy"
        currApplication.user_id?.let {
            db.reference.child("users").child(it).get().addOnSuccessListener { ds ->
                if (ds.exists()) {
                    holder.frameLayout.visibility = View.GONE
                    val user = ds.getValue(User::class.java)
                    holder.userName.text = user?.name
                    holder.mcJoApplication.setOnClickListener {
                        onApplicationClicked(currApplication)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = jobApplicationList.size

    class RvJobApplicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.tv_application_user_name)
        val tvTimeRequset: TextView = view.findViewById(R.id.tv_time_of_request)
        val frameLayout: FrameLayout =
            view.findViewById(R.id.frame_item_loading_job_application_user_list)
        val mcJoApplication: MaterialCardView = view.findViewById(R.id.mc_application_item)
    }
}